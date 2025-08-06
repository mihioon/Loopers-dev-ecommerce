package com.loopers.infrastructure.brand;

import com.loopers.domain.brand.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BrandRepositoryImpl implements BrandRepository {
    private final JPAQueryFactory queryFactory;

    private static final QBrand brand = QBrand.brand;
    private static final QBrandImage image = QBrandImage.brandImage;

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
    public BrandImage save(final BrandImage brandImage) {
        // BrandImage는 Brand를 통해서 저장됩니다
        Brand brand = brandImage.getBrand();
        if (brand != null) {
            brandJpaRepository.save(brand);
        }
        return brandImage;
    }

    @Override
    public List<BrandImage> saveAll(final List<BrandImage> brandImages) {
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
    public Optional<BrandImage> findBrandImageById(final Long brandImageId) {
        return brandJpaRepository.findBrandImageById(brandImageId);
    }

    @Override
    public List<BrandImage> findBrandImagesByBrandId(final Long brandId) {
        return brandJpaRepository.findBrandImagesByBrandId(brandId);
    }

    public Optional<Brand> findWithImagesById(Long brandId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(brand)
                        .leftJoin(brand.images, image).fetchJoin()
                        .where(brand.id.eq(brandId))
                        .fetchOne()
        );
    }

}
