package com.jeanbarcellos.project115.user.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import com.jeanbarcellos.project115.user.model.User;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository {

    private Map<Long, User> db = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserRepository() {
        this.seed(150);
    }

    // --- Métodos do Repository ---

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
            user.setVersion(LocalDateTime.now());
        }

        db.put(user.getId(), user);

        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(db.get(id));
    }

    public List<User> findAll() {
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
            User user = User.builder()
                    .id(idGenerator.getAndIncrement())
                    .name("User " + i)
                    .email("email " + i + "@test.com")
                    .version(LocalDateTime.now())
                    .build();
            db.put(user.getId(), user);
        });
    }
}
