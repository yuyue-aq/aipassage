package com.yuyue.model.enums;

import lombok.Getter;

/**
 * 文章生成阶段枚举
 */
@Getter
public enum ArticlePhaseEnum {

    INIT("INIT", "初始化"),
    TITLE("TITLE", "生成标题"),
    OUTLINE("OUTLINE", "生成大纲"),
    CONTENT("CONTENT", "生成正文"),
    IMAGE("IMAGE", "生成配图"),
    MERGE("MERGE", "图文合成"),
    COMPLETED("COMPLETED", "已完成"),
    FAILED("FAILED", "失败");

    private final String value;

    private final String description;

    ArticlePhaseEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ArticlePhaseEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (ArticlePhaseEnum phaseEnum : values()) {
            if (phaseEnum.value.equals(value)) {
                return phaseEnum;
            }
        }
        return null;
    }
}

