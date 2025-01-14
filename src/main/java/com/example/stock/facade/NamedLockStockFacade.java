package com.example.stock.facade;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.repository.LockRepository;
import com.example.stock.service.NamedLockStockService;

@Component
public class NamedLockStockFacade {

    private final LockRepository lockRepository;
    private final NamedLockStockService namedLockStockService;

    public NamedLockStockFacade(LockRepository lockRepository, NamedLockStockService namedLockStockService) {
        this.lockRepository = lockRepository;
        this.namedLockStockService = namedLockStockService;
    }

    @Transactional
    public void decrease(Long id, Long quantity) {
        try {
            lockRepository.getLock(id.toString());
            namedLockStockService.decrease(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }
}
