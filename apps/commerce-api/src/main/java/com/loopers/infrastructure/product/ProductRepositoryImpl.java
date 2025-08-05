package com.loopers.infrastructure.product;

import com.loopers.domain.catalog.product.Product;
import com.loopers.domain.catalog.product.ProductCommand;
import com.loopers.domain.catalog.product.ProductRepository;
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

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<Product> findProductsWithSort(ProductCommand.Summery command) {
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

    private Pageable createPageable(ProductCommand.Summery command) {
        Sort sort = createSort(command.sortType());
        return PageRequest.of(command.page(), command.size(), sort);
    }

    private Sort createSort(ProductCommand.Summery.SortType sortType) {
        String field = sortType.getField();
        String direction = sortType.getDirection();
        
        return "desc".equalsIgnoreCase(direction) 
                ? Sort.by(Sort.Direction.DESC, field)
                : Sort.by(Sort.Direction.ASC, field);
    }
}
