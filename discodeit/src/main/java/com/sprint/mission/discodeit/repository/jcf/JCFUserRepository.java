package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> userMap;

    private final Map<String, UUID> nameMap;
    private final Map<String, UUID> emailMap;

    public JCFUserRepository(){
        this.userMap = new HashMap<>();
        this.nameMap = new HashMap<>();
        this.emailMap = new HashMap<>();
    }

    @Override
    public void save(User user) {
        UUID id = user.getId();
        userMap.put(id, user);
        nameMap.put(user.getUserName(), id);
        emailMap.put(user.getEmail(), id);
    }

    @Override
    public boolean remove(UUID id) {
        return (userMap.remove(id) != null);
    }

    @Override
    public Optional<User> findByID(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    public Optional<User> findByUserName(String userName){
        return Optional.ofNullable(userMap.get(nameMap.get(userName)));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public boolean registUser(User user){
        UUID userId = user.getId();
        String userName = user.getUserName();
        String userEmail = user.getEmail();

        UUID nameOwner = nameMap.putIfAbsent(user.getUserName(), userId);
        if (nameOwner != null) {
            return false;
        }

        UUID emailOwner = emailMap.putIfAbsent(user.getEmail(), user.getId());
        if (emailOwner != null) {
            nameMap.remove(user.getUserName());
            return false;
        }

        try {
            save(user);
            return true;
        } catch (Exception e){
            emailMap.remove(userEmail);
            nameMap.remove(userName);
            throw e;
        }
    }

    @Override
    public boolean withdrawUser(User user){
        remove(user.getId());
        nameMap.remove(user.getUserName());
        emailMap.remove(user.getEmail());
        return true;
    }
}