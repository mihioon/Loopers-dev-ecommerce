package com.loopers.application.brand;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BrandFacade {
    private final BrandService brandService;

    public BrandResult getBrand(final Long brandId) {
        brandService.validateBrandId(brandId);
        return BrandResult.from(brandService.getBy(brandId));
    }
}
