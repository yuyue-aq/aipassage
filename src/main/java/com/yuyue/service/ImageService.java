package com.yuyue.service;

import com.yuyue.constant.ArticleConstant;
import com.yuyue.model.enums.ImageMethodEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 图片服务：封装图片检索与降级逻辑
 */
@Service
public class ImageService implements ArticleConstant {

    private final List<ImageSearchService> searchServices;

    public ImageService(List<ImageSearchService> searchServices) {
        this.searchServices = searchServices;
    }

    /**
     * 根据关键词检索图片
     */
    public String searchImage(String keywords) {
        ImageSearchService service = getPrimaryService();
        if (service == null) {
            return null;
        }
        return service.searchImage(keywords);
    }

    /**
     * 获取当前图片检索方式
     */
    public ImageMethodEnum getMethod() {
        ImageSearchService service = getPrimaryService();
        if (service == null) {
            return ImageMethodEnum.PICSUM;
        }
        return service.getMethod();
    }

    /**
     * 获取降级图片 URL
     */
    public String getFallbackImage(int position) {
        ImageSearchService service = getPrimaryService();
        if (service == null) {
            return String.format(PICSUM_URL_TEMPLATE, position);
        }
        return service.getFallbackImage(position);
    }

    private ImageSearchService getPrimaryService() {
        if (searchServices == null || searchServices.isEmpty()) {
            return null;
        }
        return searchServices.get(0);
    }
}

