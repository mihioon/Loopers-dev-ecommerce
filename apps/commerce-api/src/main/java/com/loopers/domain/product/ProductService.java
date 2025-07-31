package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProductService {
    
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductInfo.Summery getSummery(final ProductCommand.Summery command) {
        final List<Product> products = productRepository.findProductsWithSort(command);
        final long totalElements = productRepository.countProductsWithFilter(command.category(), command.brandId());

        // 페이징 정보 계산
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

        return ProductInfo.Detail.from(product, images, detail);
    }
}
