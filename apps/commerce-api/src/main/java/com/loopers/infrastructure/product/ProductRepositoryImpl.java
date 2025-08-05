package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductStock;
import com.loopers.domain.product.dto.ProductQuery;
import com.loopers.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    
    private final ProductJpaRepository productJpaRepository;
    private final ProductStockJpaRepository productStockJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<Product> findProductsWithSort(ProductQuery.Summery command) {
        Pageable pageable = createPageable(command);
        Page<Product> productPage = productJpaRepository.findProductsWithFilter(
                command.category(),
                command.brandId(),
                pageable
        );
        return productPage.getContent();
    }

    @Override
    public long countProductsWithFilter(String category, Long brandId) {
        return productJpaRepository.countProductsWithFilter(category, brandId);
    }

    @Override
    public Optional<Product> findByIdWithImagesAndDetail(Long id) {
        return productJpaRepository.findByIdWithImagesAndDetail(id);
    }

    private Pageable createPageable(ProductQuery.Summery command) {
        Sort sort = createSort(command.sortType());
        return PageRequest.of(command.page(), command.size(), sort);
    }

    private Sort createSort(ProductQuery.Summery.SortType sortType) {
        String field = sortType.getField();
        String direction = sortType.getDirection();
        
        return "desc".equalsIgnoreCase(direction) 
                ? Sort.by(Sort.Direction.DESC, field)
                : Sort.by(Sort.Direction.ASC, field);
    }

    @Override
    public ProductStock save(ProductStock productStock) {
        return productStockJpaRepository.save(productStock);
    }

    @Override
    public Optional<ProductStock> findStockByProductId(Long productId) {
        return productStockJpaRepository.findByProductId(productId);

    }
}
