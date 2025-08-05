package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Table(name = "product_stock")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductStock extends BaseEntity {

    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private Integer quantity;
    

    public ProductStock(
            final Long productId,
            final Integer quantity
    ) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다.");
        }
        if (quantity == null || quantity < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고 수량은 0 이상이어야 합니다.");
        }

        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }



    public void reduceStock(final Integer amount) {
        validateReduceAmount(amount);
        validateSufficientStock(amount);
        
        this.quantity -= amount;
        
        if (this.quantity < 0) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "재고가 음수가 되었습니다. 시스템 오류.");
        }
    }

    
    private void validateReduceAmount(final Integer amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "차감할 재고 수량은 0보다 커야 합니다.");
        }
    }
    
    void validateSufficientStock(final Integer amount) {
        if (this.quantity < amount) {
            throw new CoreException(ErrorType.BAD_REQUEST, 
                String.format("재고가 부족합니다. 현재: %d, 요청: %d", this.quantity, amount));
        }
    }
}
