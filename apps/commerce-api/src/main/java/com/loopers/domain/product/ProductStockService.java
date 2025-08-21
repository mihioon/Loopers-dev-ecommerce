package com.loopers.domain.product;

import com.loopers.domain.product.dto.ProductStockCommand;
import com.loopers.domain.product.dto.StockInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProductStockService {
    
    private final ProductRepository productRepository;

    @Transactional(rollbackFor = Exception.class)
    public StockInfo create(final ProductStockCommand.Create command) {
        final ProductStock stock = new ProductStock(command.productId(), command.quantity());
        final ProductStock savedStock = productRepository.save(stock);
        return StockInfo.from(savedStock);
    }

    @Transactional(readOnly = true)
    public StockInfo getStock(final Long productId) {
        return productRepository.findStockByProductId(productId)
                .map(StockInfo::from)
                .orElse(new StockInfo(null, productId, 0));
    }

    @Transactional(rollbackFor = Exception.class)
    public StockInfo reduceStock(final ProductStockCommand.Reduce command) {
        final ProductStock stock = productRepository.findStockByProductIdWithLock(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품 재고를 찾을 수 없습니다."));

        stock.validateSufficientStock(command.quantity());
        stock.reduceStock(command.quantity());
        final ProductStock savedStock = productRepository.save(stock);
        return StockInfo.from(savedStock);
    }

    public void validateStocks(final List<ProductStockCommand.Reduce> commands) {
        List<ProductStockCommand.Reduce> sortedCommands = sortById(commands);


        for (ProductStockCommand.Reduce command : sortedCommands) {
            final ProductStock stock = productRepository.findStockByProductIdWithLock(command.productId())
                    .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품 재고를 찾을 수 없습니다."));
            stock.validateSufficientStock(command.quantity());
        }
    }

    //validateAndReduceStocks
    @Transactional(rollbackFor = Exception.class)
    public List<StockInfo> validateAndReduceStocks(final List<ProductStockCommand.Reduce> commands) {
        List<ProductStockCommand.Reduce> sortedCommands = sortById(commands);

        return sortedCommands.stream()
                .map(this::reduceStock)
                .toList();
    }

    private List<ProductStockCommand.Reduce> sortById(final List<ProductStockCommand.Reduce> commands) {
        return commands.stream()
                .sorted((a, b) -> Long.compare(a.productId(), b.productId()))
                .toList();
    }
}
