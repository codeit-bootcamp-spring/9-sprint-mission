package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> data = new HashMap<>();
    private final Set<String> emailIndex = new HashSet<>();
    private final Set<String> phoneIndex = new HashSet<>();

    @Override
    public User save(User user) {

        User existing = data.get(user.getId());

        //create
        if (existing == null) {
            data.put(user.getId(), user);
            emailIndex.add(user.getEmail());
            phoneIndex.add(user.getPhoneNumber());
            return user;
        }

        //update
        if (!Objects.equals(existing.getEmail(), user.getEmail())) {
            emailIndex.remove(existing.getEmail());
            emailIndex.add(user.getEmail());
        }
        if (!Objects.equals(existing.getPhoneNumber(), user.getPhoneNumber())) {
            phoneIndex.remove(existing.getPhoneNumber());
            phoneIndex.add(user.getPhoneNumber());
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
    public void deleteById(UUID userId) {
        if (userId == null) return;

        User removed = data.remove(userId);
        if (removed == null) return;

        emailIndex.remove(removed.getEmail());
        phoneIndex.remove(removed.getPhoneNumber());
    }

    @Override
    public boolean existsById(UUID userId) {
        if (userId == null) return false;
        return data.containsKey(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return emailIndex.contains(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        return phoneIndex.contains(phoneNumber);
    }
}