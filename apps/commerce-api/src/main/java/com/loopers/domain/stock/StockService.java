package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class StockService {
    
    private final ProductStockRepository productStockRepository;

    @Transactional(rollbackFor = Exception.class)
    public StockInfo initializeStock(final StockCommand.Initialize command) {
        final ProductStock stock = new ProductStock(command.productId(), command.initialQuantity(), 0);
        final ProductStock savedStock = productStockRepository.save(stock);
        return StockInfo.from(savedStock);
    }

    @Transactional(readOnly = true)
    public StockInfo getStock(final Long productId) {
        return productStockRepository.findByProductId(productId)
                .map(StockInfo::from)
                .orElse(new StockInfo(null, productId, 0, 0, 0));
    }

    @Transactional(rollbackFor = Exception.class)
    public void adjustStock(final StockCommand.Adjustment command) {
        final ProductStock stock = productStockRepository.findByProductId(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품 재고를 찾을 수 없습니다."));

        switch (command.operation()) {
            case ADD -> stock.addStock(command.amount());
            case REDUCE -> stock.reduceStock(command.amount());
            case RESERVE -> stock.reserveStock(command.amount());
            case RELEASE_RESERVED -> stock.releaseReservedStock(command.amount());
        }

        productStockRepository.save(stock);
    }
}
