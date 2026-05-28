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
    FAILED("FAILED", "失败"),
    PENDING("PENDING", "等待处理"),
    TITLE_GENERATING("TITLE_GENERATING", "生成标题中"),
    TITLE_SELECTING("TITLE_SELECTING", "等待选择标题"),
    OUTLINE_GENERATING("OUTLINE_GENERATING", "生成大纲中"),
    OUTLINE_EDITING("OUTLINE_EDITING", "等待编辑大纲"),
    CONTENT_GENERATING("CONTENT_GENERATING", "生成正文中");

    /**
     * 阶段值
     */
    private final String value;

    /**
     * 阶段描述
     */
    private final String description;

    ArticlePhaseEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 阶段值
     * @return 枚举实例
     */
    public static ArticlePhaseEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (ArticlePhaseEnum phaseEnum : values()) {
            if (phaseEnum.getValue().equals(value)) {
                return phaseEnum;
            }
        }
        return null;
    }
}

