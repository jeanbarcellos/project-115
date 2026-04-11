package com.jeanbarcellos.project115.wallet.application.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import org.springframework.stereotype.Repository;

import com.jeanbarcellos.project115.wallet.domain.Wallet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WalletRepository {

    private Map<Long, Wallet> db = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public WalletRepository() {
        this.seed(10);
    }

    // --- Métodos do Repository ---

    public Wallet save(Wallet entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }

        db.put(entity.getId(), entity);

        return entity;
    }

    public Optional<Wallet> findById(Long id) {
        return Optional.ofNullable(db.get(id));
    }

    public List<Wallet> findAll() {
        return new ArrayList<>(db.values());
    }

    public void deleteById(Long id) {
        db.remove(id);
    }

    public boolean existsById(Long id) {
        return db.containsKey(id);
    }

    private void seed(int endInclusive) {
        IntStream.rangeClosed(1, endInclusive).forEach(i -> {
            Wallet entity = new Wallet(new BigDecimal(100L));
            entity.setId(idGenerator.getAndIncrement());
            db.put(entity.getId(), entity);
        });
        log.info("seed wallet");
    }
}
