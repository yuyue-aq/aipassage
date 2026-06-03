CREATE TABLE IF NOT EXISTS user
(
    id           bigint AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userAccount  varchar(256)                           NOT NULL COMMENT '账号',
    userPassword varchar(512)                           NOT NULL COMMENT '密码',
    userName     varchar(256)                           NULL COMMENT '用户昵称',
    userAvatar   varchar(1024)                          NULL COMMENT '用户头像',
    userProfile  varchar(512)                           NULL COMMENT '用户简介',
    userRole     varchar(256) DEFAULT 'user'            NOT NULL COMMENT '用户角色：user/admin',
    editTime     datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '编辑时间',
    createTime   datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete     tinyint      DEFAULT 0                 NOT NULL COMMENT '是否删除',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
) COMMENT '用户' COLLATE = utf8mb4_unicode_ci;

-- 初始化数据
-- 密码是 12345678（MD5 加密 + 盐值 yupi）
INSERT INTO user (id, userAccount, userPassword, userName, userAvatar, userProfile, userRole)
VALUES (1, 'admin', '10670d38ec32fa8102be6a37f8cb52bf', '管理员', 'https://img.yuyue-aq.com/logo.png', '系统管理员',
        'admin'),
       (2, 'user', '10670d38ec32fa8102be6a37f8cb52bf', '普通用户', 'https://img.yuyue-aq.com/logo.png',
        '我是一个普通用户', 'user'),
       (3, 'test', '10670d38ec32fa8102be6a37f8cb52bf', '测试账号', 'https://img.yuyue-aq.com/logo.png',
        '这是一个测试账号', 'user');

-- 文章表（基础字段，style/phase/titleOptions/userDescription/enabledImageMethods 由增量脚本添加）
CREATE TABLE IF NOT EXISTS article
(
    id            bigint AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    taskId        varchar(64)                           NOT NULL COMMENT '任务ID（UUID）',
    userId        bigint                                NOT NULL COMMENT '用户ID',
    topic         varchar(500)                          NOT NULL COMMENT '选题',
    mainTitle     varchar(200)                          NULL COMMENT '主标题',
    subTitle      varchar(300)                          NULL COMMENT '副标题',
    outline       json                                  NULL COMMENT '大纲（JSON格式）',
    content       text                                  NULL COMMENT '正文（Markdown格式）',
    fullContent   text                                  NULL COMMENT '完整图文（Markdown格式，含配图）',
    coverImage    varchar(512)                          NULL COMMENT '封面图 URL',
    images        json                                  NULL COMMENT '配图列表（JSON数组，包含封面图 position=1）',
    status        varchar(20) DEFAULT 'PENDING'         NOT NULL COMMENT '状态：PENDING/PROCESSING/COMPLETED/FAILED',
    errorMessage  text                                  NULL COMMENT '错误信息',
    createTime    datetime    DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    completedTime datetime                              NULL COMMENT '完成时间',
    updateTime    datetime    DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete      tinyint     DEFAULT 0                 NOT NULL COMMENT '是否删除',
    UNIQUE KEY uk_taskId (taskId),
    INDEX idx_userId (userId),
    INDEX idx_status (status),
    INDEX idx_createTime (createTime),
    INDEX idx_userId_status (userId, status)
) COMMENT '文章表' COLLATE = utf8mb4_unicode_ci;

-- 智能体执行日志表
CREATE TABLE IF NOT EXISTS agent_log
(
    id           bigint AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    taskId       varchar(64)                        NOT NULL COMMENT '任务ID',
    agentName    varchar(50)                        NOT NULL COMMENT '智能体名称',
    startTime    datetime                           NOT NULL COMMENT '开始时间',
    endTime      datetime                           NULL COMMENT '结束时间',
    durationMs   int                                NULL COMMENT '耗时（毫秒）',
    status       varchar(20)                        NOT NULL COMMENT '状态：SUCCESS/FAILED',
    errorMessage text                               NULL COMMENT '错误信息',
    prompt       text                               NULL COMMENT '使用的Prompt',
    inputData    json                               NULL COMMENT '输入数据（JSON格式）',
    outputData   json                               NULL COMMENT '输出数据（JSON格式）',
    createTime   datetime DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   datetime DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete     tinyint  DEFAULT 0                 NOT NULL COMMENT '是否删除',
    INDEX idx_taskId (taskId),
    INDEX idx_agentName (agentName),
    INDEX idx_status (status),
    INDEX idx_createTime (createTime)
) COMMENT '智能体执行日志表' COLLATE = utf8mb4_unicode_ci;
