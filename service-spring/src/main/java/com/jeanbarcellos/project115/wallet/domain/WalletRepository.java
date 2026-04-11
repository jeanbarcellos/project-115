package com.jeanbarcellos.project115.wallet.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

@Repository
public class WalletRepository {

    private Map<Long, Wallet> db = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

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
}
