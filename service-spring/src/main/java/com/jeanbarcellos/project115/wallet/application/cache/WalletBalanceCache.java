package com.jeanbarcellos.project115.wallet.application.cache;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WalletBalanceCache {

    private final ConcurrentHashMap<Long, BigDecimal> cache = new ConcurrentHashMap<>();

    public BigDecimal get(Long id) {
        return cache.get(id);
    }

    public void put(Long id, BigDecimal value) {
        cache.put(id, value);
    }

    public void evict(Long id) {
        cache.remove(id);
    }
}