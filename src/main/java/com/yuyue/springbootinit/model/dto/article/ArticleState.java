package com.yuyue.springbootinit.model.dto.article;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

public class ArticleState implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 任务ID
     */
    private String taskid;
    /**
     * 选题
     */
    private String topic;
    /**
     * 标题结果（智能体1输出）
     */
    private TitleResult title;
    /**
     * 大纲结果（智能体2输出）
     */
    private OutlineResult outline;
    /**
     * 正文内容（智能体3输出）
     */
    private String content;
    /**
     * 配图需求列表（智能体4输出）
     */
    private List<ImageRequirement> imageRequirements;
    /**
     * 封面图 URL（单独存储，同时 images 列表中的 position=1 也是封面图）
     */
    private String coverImage;
    /**
     * 配图结果列表（智能体5输出）
     */
    private List<ImageResult> images;
    /**
     * 完整图文内容（合成后）
     */
    private String fullContent;

    public Object getTaskId() {
    }

    public CharSequence getTopic() {
    }

    public void setTitle(TitleResult titleResult) {
    }

    public void setOutline(OutlineResult outlineResult) {
    }

    public Object getTitle() {
    }

    public void setContent(String content) {
    }

    public void setImageRequirements(List<ImageRequirement> imageRequirements) {
    }

    public CharSequence getContent() {
    }

    public Iterable<? extends ImageRequirement> getImageRequirements() {
    }

    public void setImages(List<ImageResult> imageResults) {
    }

    public List<ImageResult> getImages() {
        return null;
    }

    /**
     * 标题结果
     */
    @Data
    public static class TitleResult implements Serializable {
        private String mainTitle;
        private String subTitle;
    }

    /**
     * 大纲结果
     */
    @Data
    public static class OutlineResult implements Serializable {
        private List<OutlineSection>sections;
    }

    /**
     * 大纲章节
     */
    @Data
    public static class OutlineSection implements Serializable {
        private Integer sectionId;
        private String title;
        private List<String> points;
    }

    /**
     * 配图需求
     */
    @Data
    public static class ImageRequirement implements Serializable {
        private Integer position;
        private String type;
        private String sectionTitle;
        private String keywords;
    }

    /**
     * 配图结果
     */
    @Data
    public static class ImageResult implements Serializable {
        private Integer position;
        private String url;
        private String method;
        private String keywords;
        private String sectionTitle;
        private String description;
    }
}
