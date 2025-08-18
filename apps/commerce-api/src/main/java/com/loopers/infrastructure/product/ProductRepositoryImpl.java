package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductStock;
import com.loopers.domain.product.dto.ProductQuery;
import com.loopers.domain.product.dto.ProductWithLikeCountProjection;
import com.loopers.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public void deleteById(Long id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public List<Product> findByIds(List<Long> ids) {
        return productJpaRepository.findByIdIn(ids);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public Page<ProductWithLikeCountProjection> findProductsWithSort(ProductQuery.Summary command, Pageable pageable) {
        if (command.sortType() == ProductQuery.Summary.SortType.LIKES_DESC) {
            return productJpaRepository.findProductsWithFilterByLikes(command.category(), command.brandId(), pageable);
        }
        return productJpaRepository.findProductsWithFilter(command.category(), command.brandId(), pageable);
    }

    @Override
    public long countProductsWithFilter(String category, Long brandId) {
        return productJpaRepository.countProductsWithFilter(category, brandId);
    }

    @Override
    public Optional<Product> findByIdWithImagesAndDetail(Long id) {
        return productJpaRepository.findByIdWithImagesAndDetail(id);
    }

    @Override
    public ProductStock save(ProductStock productStock) {
        return productStockJpaRepository.save(productStock);
    }

    @Override
    public Optional<ProductStock> findStockByProductId(Long productId) {
        return productStockJpaRepository.findByProductId(productId);
    }

    @Override
    public Optional<ProductStock> findStockByProductIdWithLock(Long productId) {
        return productStockJpaRepository.findByProductIdWithLock(productId);
    }
}
