package com.loopers.infrastructure.brand;

import com.loopers.domain.brand.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrandJpaRepository extends JpaRepository<Brand, Long> {
    boolean existsByName(String name);

    Optional<Brand> findByName(String name);

    // BrandImage 관련 쿼리 메서드들
    @Query("SELECT bi FROM Brand b JOIN b.images bi WHERE b.id = :brandId")
    List<Brand.BrandImage> findBrandImagesByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT bi FROM Brand b JOIN b.images bi WHERE bi.id = :imageId")
    Optional<Brand.BrandImage> findBrandImageById(@Param("imageId") Long imageId);
}
