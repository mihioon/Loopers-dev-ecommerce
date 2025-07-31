package com.loopers.domain.catalog;


import com.loopers.domain.catalog.brand.Brand;
import com.loopers.domain.catalog.brand.BrandInfo;
import com.loopers.domain.catalog.brand.BrandRepository;
import com.loopers.domain.catalog.product.Product;
import com.loopers.domain.catalog.product.ProductCommand;
import com.loopers.domain.catalog.product.ProductInfo;
import com.loopers.domain.catalog.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProductBrandService {
    
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public ProductInfo.Summery getSummery(final ProductCommand.Summery command) {
        final List<Product> products = productRepository.findProductsWithSort(command);
        final long totalElements = productRepository.countProductsWithFilter(command.category(), command.brandId());

        final int currentPage = command.page();
        final int size = command.size();
        final int totalPages = (int) Math.ceil((double) totalElements / size);
        final boolean hasNext = currentPage < totalPages - 1;

        return ProductInfo.Summery.from(products, currentPage, totalPages, totalElements, hasNext);
    }

    @Transactional(readOnly = true)
    public ProductInfo.Detail getDetail(final Long productId) {
        final Product product = productRepository.findByIdWithImagesAndDetail(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

        final BrandInfo brandInfo = getBrandInfo(product.getBrandId());

        final List<ProductInfo.ImageInfo> images = product.getImages()
                .stream()
                .map(ProductInfo.ImageInfo::from)
                .toList();

        final ProductInfo.DetailInfo detail = product.getDetail() != null 
                ? ProductInfo.DetailInfo.from(product.getDetail())
                : null;
        
        if (detail == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "상품 상세 정보를 찾을 수 없습니다.");
        }

        return ProductInfo.Detail.from(product, images, detail, brandInfo);
    }

    @Transactional(readOnly = true)
    public ProductInfo.Basic getBasic(final Long productId) {
        final Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
        
        return ProductInfo.Basic.from(product);
    }

    private BrandInfo getBrandInfo(final Long brandId) {
        final Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다."));
        
        final List<BrandInfo.BrandImageInfo> images = brandRepository.findBrandImagesByBrandId(brandId)
                .stream()
                .map(BrandInfo.BrandImageInfo::from)
                .toList();
        
        return BrandInfo.from(brand, images);
    }
}
