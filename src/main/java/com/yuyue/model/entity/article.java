package com.yuyue.model.entity;

import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "article", camelToUnderline = false)
public class article {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * UUID
     */
    private String taskid;

    /**
     * userid
     */
    private String userid;


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
     * content,md格式
     */
    private String content;

    /**
     *
     */
}
