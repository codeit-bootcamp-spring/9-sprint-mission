package service.jcf;

import entity.User;
import service.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserService implements UserService {
    // ID로 찾기
    private final Map<UUID, User> userMap = new ConcurrentHashMap<>();
    // 이름으로 찾기
    private final Map<String, User> nameMap = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        // [기존] userMap에만 저장
        // [수정] 두 보관함에 모두 저장하여 이름으로도 즉시 찾을 수 있게 함
        userMap.put(user.getId(), user);
        nameMap.put(user.getDisplayName(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public Optional<User> findByDisplayName(String displayName) {
        /* [기존] 전체 데이터를 하나씩 확인하며 이름을 비교해야 해서 데이터가 많으면 느려짐
        [수정] nameMap에서 바로 꺼냄 이름보관함에서 이름key로 바로 찾기 때문에  매우 빠름*/
        return Optional.ofNullable(nameMap.get(displayName));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void update(User user) {
        // [기존] userMap에서 ID가 있으면 덮어쓰기 [수정] 이름 변경 대응 로직 추가
        User oldUser = userMap.get(user.getId());
        if (oldUser != null) {
            // 만약 이름이 바뀌었다면, 이름 보관함에서 옛날 이름을 지워줘야 함
            if (!oldUser.getDisplayName().equals(user.getDisplayName())) {
                nameMap.remove(oldUser.getDisplayName());
            }
            // 새로운 정보를 두 보관함에 갱신
            userMap.put(user.getId(), user);
            nameMap.put(user.getDisplayName(), user);
        }
    }

    @Override
    public boolean delete(UUID id) {
        // [기존] userMap에서만 삭제 [수정] 두 보관함에서 모두 삭제
        User user = userMap.remove(id);
        if (user != null) {
            nameMap.remove(user.getDisplayName());
            return true;
        }
        return false;
    }
}