package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class StockService {
    
    private final ProductStockRepository productStockRepository;

    @Transactional(rollbackFor = Exception.class)
    public StockInfo initializeStock(final StockCommand.Initialize command) {
        final ProductStock stock = new ProductStock(command.productId(), command.initialQuantity());
        final ProductStock savedStock = productStockRepository.save(stock);
        return StockInfo.from(savedStock);
    }

    @Transactional(readOnly = true)
    public StockInfo getStock(final Long productId) {
        return productStockRepository.findByProductId(productId)
                .map(StockInfo::from)
                .orElse(new StockInfo(null, productId, 0));
    }

    @Transactional(rollbackFor = Exception.class)
    public List<StockInfo> reduceAllStocks(final Long orderId, final List<StockCommand.Reduce> commands) {
        return commands.stream()
                .map(this::reduceStock)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public StockInfo reduceStock(final StockCommand.Reduce command) {
        final ProductStock stock = productStockRepository.findByProductId(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품 재고를 찾을 수 없습니다."));

        stock.reduceStock(command.amount());
        final ProductStock savedStock = productStockRepository.save(stock);
        return StockInfo.from(savedStock);
    }

    //validateAndReduceStocks
    @Transactional(rollbackFor = Exception.class)
    public List<StockInfo> validateAndReduceStocks(final List<StockCommand.Reduce> commands) {
        commands.forEach(this::validateStock);

        return commands.stream()
                .map(this::reduceStock)
                .toList();
    }

    private void validateStock(final StockCommand.Reduce command) {
        final ProductStock stock = productStockRepository.findByProductId(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품 재고를 찾을 수 없습니다."));

        stock.validateSufficientStock(command.amount());
    }

}
