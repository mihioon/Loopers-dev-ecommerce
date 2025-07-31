package com.loopers.infrastructure.brand;

import com.loopers.domain.brand.BrandImage;
import com.loopers.domain.brand.BrandImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BrandImageRepositoryImpl implements BrandImageRepository {
    private final BrandImageJpaRepository brandImageJpaRepository;

    @Override
    public BrandImage save(final BrandImage brandImage) {
        return brandImageJpaRepository.save(brandImage);
    }

    @Override
    public List<BrandImage> saveAll(final List<BrandImage> brandImages) {
        return brandImageJpaRepository.saveAll(brandImages);
    }

    @Override
    public Optional<BrandImage> findById(final Long brandImageId) {
        return brandImageJpaRepository.findById(brandImageId);
    }

    @Override
    public List<BrandImage> findByBrandId(final Long brandId) {
        return brandImageJpaRepository.findByBrandId(brandId);
    }
}
