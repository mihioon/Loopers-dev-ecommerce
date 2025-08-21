package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.StringListConverter;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "orders")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String orderUuid;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal pointAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Convert(converter = StringListConverter.class)
    private List<Long> couponIds;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(Long userId, String orderUuid, List<OrderItem> orderItems, BigDecimal totalAmount, BigDecimal pointAmount, List<Long> couponIds) {
        if (userId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID는 필수입니다.");
        }
        if (orderUuid == null || orderUuid.trim().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 ID는 필수입니다.");
        }
        if (orderItems == null || orderItems.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 항목이 없습니다.");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "총액은 0 이상이어야 합니다.");
        }
        if (pointAmount == null || pointAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 0 이상이어야 합니다.");
        }

        this.userId = userId;
        this.orderUuid = orderUuid;
        this.totalAmount = totalAmount;
        this.pointAmount = pointAmount;
        this.couponIds = couponIds;
        this.status = OrderStatus.CREATED;
        
        orderItems.forEach(item -> item.assignOrder(this));
        this.orderItems.addAll(orderItems);
    }

    public enum OrderStatus {
        CREATED, COMPLETED, CANCELLED
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getPointAmount() {
        return pointAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getOrderUuid() {
        return orderUuid;
    }
    public List<OrderItem> getOrderItems() {
        return new ArrayList<>(orderItems);
    }

    public List<Long> getCouponIds() {
        return couponIds;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
}
