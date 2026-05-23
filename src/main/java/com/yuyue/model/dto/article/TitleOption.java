package com.yuyue.model.dto.article;

import lombok.Data;

import java.io.Serializable;

/**
 * 标题方案
 */
@Data
public class TitleOption implements Serializable {

    /**
     * 主标题
     */
    private String mainTitle;

    /**
     * 副标题
     */
    private String subTitle;

    private static final long serialVersionUID = 1L;
}

