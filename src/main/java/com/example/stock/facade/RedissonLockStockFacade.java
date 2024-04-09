package com.example.stock.facade;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.example.stock.service.RedissonLockStockService;

@Component
public class RedissonLockStockFacade {
    private final RedissonClient redissonClient;
    private final RedissonLockStockService redissonLockStockService;

    public RedissonLockStockFacade(RedissonClient redissonClient, RedissonLockStockService redissonLockStockService) {
        this.redissonClient = redissonClient;
        this.redissonLockStockService = redissonLockStockService;
    }

    public void decrease(final Long id, final Long quantity) {
        final RLock lock = redissonClient.getLock(id.toString());

        try {
            boolean isAvailable = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if (!isAvailable) {
                System.out.println("Lock 획득 실패");
                return;
            }
            redissonLockStockService.decrease(id, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
