ALTER TABLE user
    ADD COLUMN quota int DEFAULT 5 NOT NULL COMMENT '剩余配额' AFTER userRole;

UPDATE user
SET quota = 5
WHERE quota IS NULL;
