package com.example.stock.facade;

import org.springframework.stereotype.Component;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.LettuceLockStockService;

@Component
public class LettuceLockStockFacade {
    private final RedisLockRepository redisLockRepository;
    private final LettuceLockStockService lettuceLockStockService;

    public LettuceLockStockFacade(RedisLockRepository redisLockRepository,
        LettuceLockStockService lettuceLockStockService) {
        this.redisLockRepository = redisLockRepository;
        this.lettuceLockStockService = lettuceLockStockService;
    }

    public void decrease(final Long id, final Long quantity) throws InterruptedException {
        while (!redisLockRepository.lock(id)) {
            Thread.sleep(100);
        }
        try {
            lettuceLockStockService.decrease(id, quantity);
        } finally {
            redisLockRepository.unlock(id);
        }
    }
}
