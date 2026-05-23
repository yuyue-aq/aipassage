package com.yuyue.model.dto.article;

import com.yuyue.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章查询请求
 */
@Data
public class ArticleQueryRequest extends PageRequest implements Serializable {

    /**
     * 文章状态（可选）
     */
    private String status;

    /**
     * 用户 ID（管理员可用）
     */
    private Long userId;

    /**
     * 选题关键词（可选）
     */
    private String topicKeyword;

    private static final long serialVersionUID = 1L;
}

