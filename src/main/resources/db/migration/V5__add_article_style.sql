ALTER TABLE article
    ADD COLUMN style VARCHAR(20) NULL COMMENT '文章风格：tech/emotional/educational/humorous' AFTER topic;
