package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public enum ImageType {
    LOGO,
    BANNER, 
    THUMBNAIL;

    public static ImageType from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미지 타입은 필수입니다.");
        }

        for (ImageType imageType : ImageType.values()) {
            if (imageType.name().equalsIgnoreCase(value.trim())) {
                return imageType;
            }
        }

        throw new CoreException(ErrorType.BAD_REQUEST, 
            "지원하지 않는 이미지 타입입니다: " + value + ". 지원 타입: LOGO, BANNER, THUMBNAIL");
    }
}
