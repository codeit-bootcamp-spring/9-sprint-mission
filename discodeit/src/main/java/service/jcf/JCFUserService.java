package service.jcf;

import entity.User;
import service.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userMap = new ConcurrentHashMap<>();
    private final Map<String, User> nameMap = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
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
        return Optional.ofNullable(nameMap.get(displayName));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void update(User user) {
        // [기존] userMap에서 ID가 있으면 덮어쓰기 ->[수정] 이름 변경 대응 로직 추가
        User oldUser = userMap.get(user.getId());
        if (oldUser != null) {
            // 만약 이름이 바뀌면, 이름 보관함에서 전이름ㅇ지우기
            if (!oldUser.getDisplayName().equals(user.getDisplayName())) {
                nameMap.remove(oldUser.getDisplayName());
            }
            // 새로운 정보를 두 보관함에 갱신
            userMap.put(user.getId(), user);
            nameMap.put(user.getDisplayName(), user);
        }
    }/*지금 기존이름 삭제랑 새이름 삽입 별도로 해결중인데..
    삭제성공삽입전에러나면어캄..Transaction...? 실무라면 어떻게하는지 궁금합니다*/

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