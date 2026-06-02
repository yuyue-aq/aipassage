package com.yuyue.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.system.SystemUtil;
import com.yuyue.config.MermaidConfig;
import com.yuyue.model.dto.image.ImageData;
import com.yuyue.model.dto.image.ImageRequest;
import com.yuyue.model.enums.ImageMethodEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.yuyue.constant.ArticleConstant.PICSUM_URL_TEMPLATE;

/**
 * Mermaid 流程图生成服务
 * 使用 mermaid-cli 将 Mermaid 代码转换为图片
 *
 */
@Service
@Slf4j
public class MermaidService implements ImageSearchService {

    @Resource
    private MermaidConfig mermaidConfig;

    @Override
    public String searchImage(String keywords) {
        // 此方法已废弃，请使用 getImageData()
        return null;
    }

    @Override
    public String getImage(ImageRequest request) {
        // 此方法已废弃，请使用 getImageData()
        return null;
    }

    @Override
    public ImageData getImageData(ImageRequest request) {
        // 优先使用 prompt（Mermaid 代码），否则使用 keywords
        String mermaidCode = request.getEffectiveParam(true);
        return generateDiagramData(mermaidCode);
    }

    /**
     * 生成 Mermaid 图表数据
     *
     * @param mermaidCode Mermaid 代码
     * @return 图片字节数据，生成失败返回 null
     */
    public ImageData generateDiagramData(String mermaidCode) {
        if (mermaidCode == null || mermaidCode.trim().isEmpty()) {
            log.warn("Mermaid 代码为空");
            return null;
        }

        // 清理 LLM 可能添加的额外文字前缀和 markdown 代码块标记
        String cleanedCode = cleanMermaidCode(mermaidCode);
        log.debug("Mermaid 原始代码: {}", mermaidCode);
        log.debug("Mermaid 清理后: {}", cleanedCode);

        File tempInputFile = null;
        File tempOutputFile = null;

        try {
            // 创建临时输入文件
            tempInputFile = FileUtil.createTempFile("mermaid_input_", ".mmd", true);
            FileUtil.writeUtf8String(cleanedCode, tempInputFile);

            // 创建临时输出文件
            String outputExtension = "." + mermaidConfig.getOutputFormat();
            tempOutputFile = FileUtil.createTempFile("mermaid_output_", outputExtension, true);

            // 转换为图片
            convertMermaidToImage(tempInputFile, tempOutputFile);

            // 检查输出文件
            if (!tempOutputFile.exists() || tempOutputFile.length() == 0) {
                log.error("Mermaid CLI 执行失败，输出文件不存在或为空");
                return null;
            }

            // 读取图片字节数据
            byte[] imageBytes = FileUtil.readBytes(tempOutputFile);
            String mimeType = getMimeType(mermaidConfig.getOutputFormat());

            log.info("Mermaid 图表生成成功, size={} bytes", imageBytes.length);
            return ImageData.fromBytes(imageBytes, mimeType);

        } catch (Exception e) {
            log.error("Mermaid 图表生成异常", e);
            return null;
        } finally {
            // 清理临时文件
            if (tempInputFile != null) {
                FileUtil.del(tempInputFile);
            }
            if (tempOutputFile != null) {
                FileUtil.del(tempOutputFile);
            }
        }
    }

    /**
     * 根据输出格式获取 MIME 类型
     */
    private String getMimeType(String format) {
        return switch (format.toLowerCase()) {
            case "png" -> "image/png";
            case "svg" -> "image/svg+xml";
            case "pdf" -> "application/pdf";
            default -> "image/png";
        };
    }

    /**
     * 调用 Mermaid CLI 转换为图片
     */
    private void convertMermaidToImage(File inputFile, File outputFile) throws Exception {
        // 根据操作系统选择命令
        String command = SystemUtil.getOsInfo().isWindows() ? "mmdc.cmd" : mermaidConfig.getCliCommand();

        // 构建命令行参数
        ProcessBuilder pb = new ProcessBuilder(
                command,
                "-i", inputFile.getAbsolutePath(),
                "-o", outputFile.getAbsolutePath(),
                "-b", mermaidConfig.getBackgroundColor()
        );

        // 如果配置了宽度，添加宽度参数
        if (mermaidConfig.getWidth() != null && mermaidConfig.getWidth() > 0) {
            pb.command().add("-w");
            pb.command().add(String.valueOf(mermaidConfig.getWidth()));
        }

        // 如果配置了 Puppeteer Chrome 路径，设置环境变量
        String puppeteerPath = mermaidConfig.getPuppeteerExecutablePath();
        if (puppeteerPath != null && !puppeteerPath.isBlank()) {
            pb.environment().put("PUPPETEER_EXECUTABLE_PATH", puppeteerPath);
        }

        log.info("执行 Mermaid CLI 命令: {}", String.join(" ", pb.command()));

        Process process = pb.start();

        // 等待命令执行完成（带超时）
        boolean finished = process.waitFor(mermaidConfig.getTimeout(), TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("Mermaid CLI 执行超时 (" + mermaidConfig.getTimeout() + "ms)");
        }

        int exitCode = process.exitValue();
        String stdout = IoUtil.read(process.getInputStream(), StandardCharsets.UTF_8);
        String stderr = IoUtil.read(process.getErrorStream(), StandardCharsets.UTF_8);

        if (exitCode != 0) {
            log.error("Mermaid CLI 执行失败, exitCode={}, stderr={}", exitCode, stderr);
            throw new RuntimeException("Mermaid CLI 执行失败 (exitCode=" + exitCode + "): " + stderr);
        }

        log.debug("Mermaid CLI 执行结果: {}", stdout);
    }

    @Override
    public ImageMethodEnum getMethod() {
        return ImageMethodEnum.MERMAID;
    }

    @Override
    public String getFallbackImage(int position) {
        return String.format(PICSUM_URL_TEMPLATE, position);
    }

    /**
     * 清理 LLM 生成的 Mermaid 代码，去除可能的前缀文字和 markdown 标记
     * <p>
     * LLM 经常会在 Mermaid 代码前添加描述性文字（如 "Mermaid flowchart code:"），
     * 或用 markdown 代码块包裹。此方法提取纯 Mermaid 语法代码。
     *
     * @param rawCode LLM 原始输出
     * @return 清理后的纯 Mermaid 代码
     */
    private String cleanMermaidCode(String rawCode) {
        String code = rawCode.trim();

        // 1. 去除 markdown 代码块标记: ```mermaid ... ``` 或 ``` ... ```
        if (code.startsWith("```")) {
            int firstNewline = code.indexOf('\n');
            if (firstNewline > 0) {
                code = code.substring(firstNewline + 1);
            }
            code = code.trim();
            int lastBackticks = code.lastIndexOf("```");
            if (lastBackticks >= 0) {
                code = code.substring(0, lastBackticks);
            }
            code = code.trim();
        }

        // 2. 去除常见的 LLM 文字前缀，提取以 Mermaid 关键字开头的部分
        // Mermaid 图表类型关键字
        String[] mermaidKeywords = {
                "flowchart", "graph", "sequenceDiagram", "classDiagram",
                "stateDiagram", "erDiagram", "gantt", "pie", "gitGraph",
                "mindmap", "timeline", "journey", "quadrantChart",
                "sankey", "xychart", "block", "packet"
        };

        for (String keyword : mermaidKeywords) {
            int idx = code.indexOf(keyword);
            if (idx > 0) {
                // 找到了关键字，从关键字位置开始截取
                // 但要确保它前面是空格或换行（避免误匹配变量名等）
                char before = code.charAt(idx - 1);
                if (before == ' ' || before == '\n' || before == '\r' || before == '\t' || before == ':' || before == '：') {
                    log.info("Mermaid 代码检测到关键字 '{}'，去除前缀: {}", keyword, code.substring(0, idx).trim());
                    code = code.substring(idx).trim();
                    break;
                }
            }
        }

        // 3. 丢弃无关前导行（例如 [blocks]）并定位首个 Mermaid 关键字行
        String[] lines = code.split("\\R");
        int start = 0;
        while (start < lines.length) {
            String line = lines[start].trim();
            if (line.isEmpty()) {
                start++;
                continue;
            }
            if (line.matches("^\\[[^\\]]+\\]\\s*$")) {
                log.info("Mermaid 代码检测到标题行，已忽略: {}", line);
                start++;
                continue;
            }
            break;
        }

        int keywordLine = -1;
        for (int i = start; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.matches("^(flowchart|graph|sequenceDiagram|classDiagram|stateDiagram|erDiagram|gantt|pie|gitGraph|mindmap|timeline|journey|quadrantChart|sankey|xychart|block|packet)\\b.*")) {
                keywordLine = i;
                break;
            }
        }

        if (keywordLine >= 0) {
            if (keywordLine > 0) {
                log.info("Mermaid 代码去除前导行: {}", String.join(" ", Arrays.copyOfRange(lines, 0, keywordLine)).trim());
            }
            code = String.join("\n", Arrays.copyOfRange(lines, keywordLine, lines.length)).trim();
            return code;
        }

        if (start > 0) {
            code = String.join("\n", Arrays.copyOfRange(lines, start, lines.length)).trim();
        }

        return sanitizeMermaidLabels(code);
    }

    /**
     * 将节点标签中的双引号替换为单引号，避免 flowchart 语法解析异常。
     */
    private String sanitizeMermaidLabels(String code) {
        StringBuilder sb = new StringBuilder(code.length());
        boolean inBracketLabel = false;
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            if (ch == '[') {
                inBracketLabel = true;
                sb.append(ch);
                continue;
            }
            if (ch == ']') {
                inBracketLabel = false;
                sb.append(ch);
                continue;
            }
            if (inBracketLabel && ch == '"') {
                sb.append('\'');
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    @Override
    public boolean isAvailable() {
        try {
            // 检查 mermaid-cli 是否已安装
            String command = SystemUtil.getOsInfo().isWindows() ? "mmdc.cmd" : mermaidConfig.getCliCommand();
            String checkCmd = command + " --version";
            String version = RuntimeUtil.execForStr(checkCmd);
            log.info("Mermaid CLI 版本: {}", version);
            return version != null && !version.isEmpty();
        } catch (Exception e) {
            log.warn("Mermaid CLI 不可用: {}", e.getMessage());
            return false;
        }
    }
}
