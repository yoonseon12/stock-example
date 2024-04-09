package com.example.stock.facade;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@SpringBootTest
class NamedLockStockFacadeTest {

    private static final Long stockId = 1L;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private NamedLockStockFacade namedLockStockFacade;

    @BeforeEach
    public void before() {
        stockRepository.saveAndFlush(new Stock(stockId, 100L));
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    void 네임드락_동시에_100개_요청() throws InterruptedException {
        final int threadCount = 100;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    namedLockStockFacade.decrease(stockId, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        final Stock findStock = stockRepository.findById(stockId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

        assertThat(findStock.getQuantity()).isEqualTo(0);
    }
}