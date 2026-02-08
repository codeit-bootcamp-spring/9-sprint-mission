package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> data = new HashMap<>();
    private final Set<String> usernameIndex = new HashSet<>();
    private final Set<String> emailIndex = new HashSet<>();
    private final Set<String> phoneIndex = new HashSet<>();

    private String norm(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }

    @Override
    public synchronized User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user is null");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("user.id is null");
        }

        User existing = data.get(user.getId());

        // create
        if (existing == null) {
            data.put(user.getId(), user);
            usernameIndex.add(norm(user.getUsername()));
            emailIndex.add(norm(user.getEmail()));
            phoneIndex.add(norm(user.getPhoneNumber()));
            return user;
        }
        if (!Objects.equals(existing.getUsername(), user.getUsername())) {
            usernameIndex.remove(norm(existing.getUsername()));
            usernameIndex.add(norm(user.getUsername()));
        }
        if (!Objects.equals(existing.getEmail(), user.getEmail())) {
            emailIndex.remove(norm(existing.getEmail()));
            emailIndex.add(norm(user.getEmail()));
        }
        if (!Objects.equals(existing.getPhoneNumber(), user.getPhoneNumber())) {
            phoneIndex.remove(norm(existing.getPhoneNumber()));
            phoneIndex.add(norm(user.getPhoneNumber()));
        }

        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        if (userId == null) return Optional.empty();
        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public synchronized void delete(UUID userId) {
        if (userId == null) return;

        User removed = data.remove(userId);
        if (removed == null) return;

        usernameIndex.remove(norm(removed.getUsername()));
        emailIndex.remove(norm(removed.getEmail()));
        phoneIndex.remove(norm(removed.getPhoneNumber()));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        String target = norm(email);

        return data.values().stream()
                .filter(u -> u.getEmail() != null)
                .filter(u -> norm(u.getEmail()).equals(target))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) return Optional.empty();
        String target = norm(username);

        return data.values().stream()
                .filter(u -> u.getUsername() != null)
                .filter(u -> norm(u.getUsername()).equals(target))
                .findFirst();
    }

    @Override
    public boolean existsById(UUID userId) {
        if (userId == null) return false;
        return data.containsKey(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return emailIndex.contains(norm(email));
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) return false;
        return phoneIndex.contains(norm(phoneNumber));
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.isBlank()) return false;
        return usernameIndex.contains(norm(username));
    }
}