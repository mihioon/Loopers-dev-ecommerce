package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Table(name = "product_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_like_product_user", columnNames = {"product_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_product_like_product_id", columnList = "product_id"),
                @Index(name = "idx_product_like_user_id", columnList = "user_id")
        })
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductLike extends BaseEntity {

    private Long productId;
    private Long userId;

    public ProductLike(
            final Long productId,
            final Long userId
    ) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다.");
        }
        if (userId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID는 필수입니다.");
        }

        this.productId = productId;
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getUserId() {
        return userId;
    }
}
