package ru.javawebinar.topjava.repository.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.AbstractBaseEntity;
import ru.javawebinar.topjava.model.AbstractNamedEntity;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepositoryImpl implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepositoryImpl.class);

    static List<User> users = Arrays.asList(
            new User(1, "user", "user@mail.ru", "password", Role.ROLE_USER),
            new User(2, "admin", "admin@mail.ru", "admin", Role.ROLE_ADMIN),
            new User(3, "admin", "admin1@mail.ru", "admin1", Role.ROLE_ADMIN),
            new User(4, "admin", "admin2@mail.ru", "admin2", Role.ROLE_ADMIN));




    private Map<Integer, User> repository = new ConcurrentHashMap<>();
    private AtomicInteger atomicInteger = new AtomicInteger(0);


    {
        users.forEach(this::save);
    }

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        return repository.remove(id) != null;

    }

    @Override
    public User save(User user) {
        log.info("save {}", user);
        if (user.isNew()) {
            user.setId(atomicInteger.incrementAndGet());
            repository.put(user.getId(), user);
            return user;
        }
        return repository.computeIfAbsent(user.getId(), (id) -> user);
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return repository.getOrDefault(id, null);
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        return repository
                .values()
                .stream()
                .sorted(Comparator.comparing(User::getName).thenComparing(User::getEmail))
                .collect(Collectors.toList());
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        return repository
                .values()
                .stream()
                .filter(e -> e.getEmail().equalsIgnoreCase(email))
                .findAny()
                .orElse(null);
    }
}
