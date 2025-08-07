package com.loopers.support;

import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.product.ProductStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class TestHelper {
    private final ProductStatusRepository productStatusRepository;

    public TestHelper(ProductStatusRepository productStatusRepository) {
        this.productStatusRepository = productStatusRepository;
    }

    public void prepareLikeCount(Long... productIds) {
        for (Long productId : productIds) {
            productStatusRepository.save(new ProductStatus(productId));
        }
    }
}
