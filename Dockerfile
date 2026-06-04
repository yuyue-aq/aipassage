ARG java_runtime_dir="/javaruntime"
FROM eclipse-temurin:21 AS builder

WORKDIR /app

ARG java_runtime_dir
ARG mvn_settings_dest="/root/.m2/settings.xml"

RUN ${JAVA_HOME}/bin/jlink \
--add-modules java.se,jdk.charsets,jdk.crypto.ec,jdk.zipfs \
--strip-debug \
--no-man-pages \
--no-header-files \
--compress=zip-9 \
--output ${java_runtime_dir} \
&& mkdir -p "$(dirname ${mvn_settings_dest})" \
&& cat > "${mvn_settings_dest}" <<'EOF'
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <mirror>
            <id>nexus-tencentyun</id>
            <mirrorOf>*</mirrorOf>
            <name>Nexus tencentyun</name>
            <url>https://mirrors.cloud.tencent.com/nexus/repository/maven-public/</url>
        </mirror>
    </mirrors>
</settings>
EOF


## 解决依赖
COPY ["./mvnw", "./mvnw.cmd", "./"]
COPY ["./.mvn", "./.mvn/"]
COPY ["./pom.xml", "./"]
RUN chmod +x ./mvnw \
&& ./mvnw -s "${mvn_settings_dest}" dependency:resolve

## 打包应用
COPY ["./src", "./src"]
RUN ./mvnw -s "${mvn_settings_dest}" clean package -DskipTests

FROM node:24

ARG java_runtime_dir

ENV JAVA_HOME="/opt/java/openjdk"
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=builder ["${java_runtime_dir}", "${JAVA_HOME}"]

ARG PUPPETEER_SKIP_CHROMIUM_DOWNLOAD="true"
ARG PUPPETEER_SKIP_DOWNLOAD="true"
ENV MERMAID_PUPPETEER_EXECUTABLE_PATH="/usr/bin/chromium"
ENV PUPPETEER_DANGEROUS_NO_SANDBOX="true"

RUN java -version \
&& node -v \
&& npm config set registry https://mirrors.cloud.tencent.com/npm/ \
&& npm install -g @mermaid-js/mermaid-cli \
&& sed -i 's/deb.debian.org/mirrors.cloud.tencent.com/g' /etc/apt/sources.list.d/debian.sources \
&& apt-get update && apt-get install -y \
chromium \
fonts-liberation fonts-noto-cjk \
--no-install-recommends \
&& rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder ["/app/target/*.jar", "./app.jar"]

# 暴露应用端口（根据实际应用配置调整）
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
