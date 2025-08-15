package com.loopers.domain.product.dto;

import com.loopers.domain.product.Product;

public interface ProductWithLikeCountProjection {
    Product getProduct();
    Long getLikeCount();
}
