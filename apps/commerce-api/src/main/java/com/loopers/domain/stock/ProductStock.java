package com.loopers.domain.stock;

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
    
    @Column(nullable = false)
    private Integer reservedQuantity;

    public ProductStock(
            final Long productId,
            final Integer quantity,
            final Integer reservedQuantity
    ) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다.");
        }
        if (quantity == null || quantity < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고 수량은 0 이상이어야 합니다.");
        }
        if (reservedQuantity == null || reservedQuantity < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "예약 수량은 0 이상이어야 합니다.");
        }

        this.productId = productId;
        this.quantity = quantity;
        this.reservedQuantity = reservedQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public void addStock(final Integer amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "추가할 재고 수량은 0보다 커야 합니다.");
        }
        this.quantity += amount;
    }

    public void reduceStock(final Integer amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "차감할 재고 수량은 0보다 커야 합니다.");
        }
        if (getAvailableQuantity() < amount) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용 가능한 재고가 부족합니다.");
        }
        this.quantity -= amount;
    }

    public void reserveStock(final Integer amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "예약할 재고 수량은 0보다 커야 합니다.");
        }
        if (getAvailableQuantity() < amount) {
            throw new CoreException(ErrorType.BAD_REQUEST, "예약 가능한 재고가 부족합니다.");
        }
        this.reservedQuantity += amount;
    }

    public void releaseReservedStock(final Integer amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "해제할 예약 수량은 0보다 커야 합니다.");
        }
        if (this.reservedQuantity < amount) {
            throw new CoreException(ErrorType.BAD_REQUEST, "해제할 수 있는 예약 수량이 부족합니다.");
        }
        this.reservedQuantity -= amount;
    }
}
