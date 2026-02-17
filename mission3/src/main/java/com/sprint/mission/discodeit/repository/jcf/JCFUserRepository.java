//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
//
//public class JCFUserRepository implements UserRepository {
//    private final Map<UUID, User> data;
//
//    public JCFUserRepository() {
//        this.data = new HashMap<>();
//    }
//
//    @Override
//    public User save(User user) {
//        this.data.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public Optional<User> findById(UUID id) {
//        return Optional.ofNullable(this.data.get(id));
//    }
//
//    @Override
//    public List<User> findAll() {
//        return this.data.values().stream().toList();
//    }
//
//    @Override
//    public boolean existsByName(String username) {
//        return this.data.containsKey(username);
//    }
//
//    @Override
//    public boolean existsByEmail(String email) {
//        return this.data.containsKey(email);
//    }
//
//    @Override
//    public boolean existsById(UUID id) {
//        return this.data.containsKey(id);
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        this.data.remove(id);
//    }
//}
