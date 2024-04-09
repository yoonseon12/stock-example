package com.example.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@Service
@Transactional(readOnly = true)
public class NamedLockStockService {

    private final StockRepository stockRepository;

    public NamedLockStockService(final StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // 부모 트랜잭션과 별도로 실행되어야 되기 때문에 별도의 프로퍼게이션을 설정
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(final Long id, final Long quantity) {
        final Stock findStock = findStockById(id);
        findStock.decrease(quantity);

        stockRepository.saveAndFlush(findStock);
    }

    public Stock findStockById(final Long id) {
        return stockRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));
    }
}
