package com.loopers.application.stock;

import com.loopers.domain.auth.AuthService;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockInfo;
import com.loopers.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StockFacade {
    
    private final StockService stockService;
    private final AuthService authService;

    public StockInfo initializeStock(final StockCriteria.Initialize criteria) {
        // 관리자 권한 검증 (필요시)
        authService.resolveUserId(criteria.loginId())
                .orElseThrow(() -> new IllegalArgumentException("인증이 필요합니다."));
        
        final StockCommand.Initialize command = new StockCommand.Initialize(
                criteria.productId(),
                criteria.initialQuantity()
        );
        
        return stockService.initializeStock(command);
    }

    public StockInfo getStock(final Long productId) {
        return stockService.getStock(productId);
    }

    public StockInfo reduceStock(final StockCriteria.Reduce criteria) {
        authService.resolveUserId(criteria.loginId())
                .orElseThrow(() -> new IllegalArgumentException("인증이 필요합니다."));
        
        final StockCommand.Reduce command = new StockCommand.Reduce(
                criteria.productId(),
                criteria.amount()
        );
        
        return stockService.reduceStock(command);
    }
}
