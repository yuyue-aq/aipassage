package com.yuyue.model.dto.image;

import lombok.Data;

import java.util.Base64;

@Data
public class ImageData {

    public enum DataType {
        BYTES,
        URL,
        DATA_URL
    }

    /**
     * 图片数据类型
     */
    private DataType dataType;

    /**
     * 原始字节数据（DataType.BYTES）
     */
    private byte[] bytes;

    /**
     * 图片 URL（DataType.URL）
     */
    private String url;

    /**
     * data URL（DataType.DATA_URL）
     */
    private String dataUrl;

    /**
     * MIME 类型（可选）
     */
    private String mimeType;

    /**
     * 是否为有效数据
     */
    public boolean isValid() {
        if (dataType == null) {
            return false;
        }
        return switch (dataType) {
            case BYTES -> bytes != null && bytes.length > 0;
            case URL -> url != null && !url.isBlank();
            case DATA_URL -> dataUrl != null && !dataUrl.isBlank();
        };
    }

    /**
     * 解析 data URL 并返回字节数组
     */
    public byte[] getImageBytes() {
        if (dataUrl == null || dataUrl.isBlank()) {
            return null;
        }
        String[] parts = dataUrl.split(",", 2);
        if (parts.length != 2) {
            return null;
        }
        String header = parts[0];
        String base64Data = parts[1];

        if (mimeType == null || mimeType.isBlank()) {
            this.mimeType = extractMimeType(header);
        }

        try {
            return Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String extractMimeType(String header) {
        if (header == null) {
            return null;
        }
        // Example: data:image/png;base64
        int colonIndex = header.indexOf(':');
        int semicolonIndex = header.indexOf(';');
        if (colonIndex < 0 || semicolonIndex < 0 || semicolonIndex <= colonIndex) {
            return null;
        }
        return header.substring(colonIndex + 1, semicolonIndex);
    }
}
