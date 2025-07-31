package com.loopers.application.brand;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BrandFacade {
    private final BrandService brandService;

    public BrandResult getBrand(final Long brandId) {
        final BrandInfo brandInfo = brandService.get(brandId);

        if (brandInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다.");
        }

        return BrandResult.from(brandInfo);
    }
}
