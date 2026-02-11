package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        this.data.put(user.getId(), user);
        return user;
    }
//매개변수인 user를 가져와 id를 호출하고 그 유저의 정보를 데이터에 저장한다. 반환값으로 user를 받는다

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }
//매개변수로 받아온 id(UUID)에 해당하는 User가 있으면 데이터에 담아서 반환하고, 없으면 Optional을 반환한다
    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }
//data(Map)에 저장된 모든 User를 List로 만들어서 반환한다
    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }
    //id에 해당되는 데이터가 존재하는지 true/false

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
    //id에 해당되는 데이터를 삭제한다

    @Override
    public Optional<User> findByUsername(String username) {
        return data.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
}
//findByUsername 메서드를 stream 방식으로 실행하는데 필터를 거쳐 status의 id가 매개변수로 받은 id랑 같은면
//가장 첫번째 UserStatus 반환한다 리턴값(Optional)
