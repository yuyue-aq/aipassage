package com.yuyue.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "article", camelToUnderline = false)
public class Article {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id,主键自增
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * UUID
     */
    @Column("taskid")
    private String taskId;

    /**
     * userid
     */
    @Column("userid")
    private Long userId;


    /**
     * 文章主题
     */
    private String topic;

    /**
     * mainTitle
     */
    private String mainTitle;

    /**
     * subTitle
     */
    private String subTitle;

    /**
     * outline
     */
    private String outline;

    /**
     * 标题方案列表（JSON格式）
     */
    private String titleOptions;

    /**
     * content,md格式
     */
    private String content;

    /**
     * content with picture
     */
    private String fullContent;
    /**
     * 封面图 URL
     */
    private String coverImage;

    /**
     * 配图列表（JSON数组）
     */
    private String images;

    /**
     * 状态：PENDING/PROCESSING/COMPLETED/FAILED
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    private LocalDateTime completedTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column(isLogicDelete = true)
    private Integer isDelete;

    /**
     * 文章风格：tech/emotional/educational/humorous
     */
    private String style;

    /**
     * 用户补充描述
     */
    private String userDescription;

    /**
     * 允许的配图方式列表（JSON格式）
     */
    private String enabledImageMethods;

    /**
     * 当前阶段：PENDING/TITLE_GENERATING/TITLE_SELECTING/OUTLINE_GENERATING/OUTLINE_EDITING/CONTENT_GENERATING
     */
    private String phase;

}
