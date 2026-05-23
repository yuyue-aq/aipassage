package com.yuyue.model.enums;

import lombok.Getter;

/**
 * 图片获取方式枚举
 */
@Getter
public enum ImageMethodEnum {

    PEXELS("pexels", "Pexels检索"),
    PICSUM("picsum", "Picsum随机图"),
    AI("ai", "AI生成");

    private final String value;

    private final String description;

    ImageMethodEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ImageMethodEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (ImageMethodEnum methodEnum : values()) {
            if (methodEnum.value.equals(value)) {
                return methodEnum;
            }
        }
        return null;
    }
}

