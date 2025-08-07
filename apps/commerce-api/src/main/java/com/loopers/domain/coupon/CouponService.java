package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CouponService {
    private final CouponRepository couponRepository;
    private final UseCouponProcessor useCouponProcessor;

    @Transactional
    public CouponInfo.Issue issueCouponToUser(Long couponId, Long userId) {
        Coupon couponTemplate = couponRepository.findById(couponId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 쿠폰입니다."));

        if (!couponTemplate.hasStock()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 재고가 없습니다.");
        }

        couponTemplate.decreaseStock();

        IssuedCoupon issuedCoupon = new IssuedCoupon(
                couponTemplate,
                userId,
                ZonedDateTime.now().plusDays(couponTemplate.getExpiresInDays())
        );

        return CouponInfo.Issue.from(couponRepository.save(issuedCoupon));
    }

    public void useIssuedCoupon(Long issuedCouponId) {
        try {
            useCouponProcessor.useCoupon(issuedCouponId);
        } catch (ObjectOptimisticLockingFailureException | StaleObjectStateException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미 사용된 쿠폰입니다.");
        }
    }

    public List<CouponInfo.Discount> getByIds(List<Long> couponIds, Long userId){
        return null;
    }

    public BigDecimal calculateTotalAmount(List<CouponInfo.Discount> couponDiscounts) {
        return null;
    }
}
