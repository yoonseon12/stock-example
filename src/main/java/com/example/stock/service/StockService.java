package com.example.stock.service;

import org.springframework.stereotype.Service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@Service
// @Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    public StockService(final StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // @Transactional
    public synchronized void decrease(final Long id, final Long quantity) {
        final Stock findStock = findStockById(id);
        findStock.decrease(quantity);

        stockRepository.saveAndFlush(findStock);
    }

    public Stock findStockById(final Long id) {
        return stockRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));
    }
}
