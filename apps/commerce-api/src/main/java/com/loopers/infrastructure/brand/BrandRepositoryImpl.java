package com.loopers.infrastructure.brand;

import com.loopers.domain.catalog.brand.Brand;
import com.loopers.domain.catalog.brand.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BrandRepositoryImpl implements BrandRepository {
    private final BrandJpaRepository brandJpaRepository;

    @Override
    public Brand save(final Brand brand) {
        return brandJpaRepository.save(brand);
    }

    @Override
    public Boolean existsByName(final String name) {
        return brandJpaRepository.existsByName(name);
    }

    @Override
    public Optional<Brand> findById(final Long brandId) {
        return brandJpaRepository.findById(brandId);
    }

    // BrandImage 관련 메서드들
    @Override
    public Brand.BrandImage save(final Brand.BrandImage brandImage) {
        // BrandImage는 Brand를 통해서 저장됩니다
        Brand brand = brandImage.getBrand();
        if (brand != null) {
            brandJpaRepository.save(brand);
        }
        return brandImage;
    }

    @Override
    public List<Brand.BrandImage> saveAll(final List<Brand.BrandImage> brandImages) {
        // 각 BrandImage의 Brand를 저장
        brandImages.forEach(image -> {
            Brand brand = image.getBrand();
            if (brand != null) {
                brandJpaRepository.save(brand);
            }
        });
        return brandImages;
    }

    @Override
    public Optional<Brand.BrandImage> findBrandImageById(final Long brandImageId) {
        return brandJpaRepository.findBrandImageById(brandImageId);
    }

    @Override
    public List<Brand.BrandImage> findBrandImagesByBrandId(final Long brandId) {
        return brandJpaRepository.findBrandImagesByBrandId(brandId);
    }
}
