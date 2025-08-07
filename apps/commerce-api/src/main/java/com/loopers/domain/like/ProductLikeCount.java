package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Table(name = "product_like_count")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductLikeCount extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long productId;
    
    @Column(nullable = false)
    private Integer likeCount;

    public ProductLikeCount(Long productId) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다.");
        }
        
        this.productId = productId;
        this.likeCount = 0;
    }

    public void increase() {
        this.likeCount++;
    }

    public void decrease() {
        if (this.likeCount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "좋아요 수는 0보다 작을 수 없습니다.");
        }
        this.likeCount--;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }
}
