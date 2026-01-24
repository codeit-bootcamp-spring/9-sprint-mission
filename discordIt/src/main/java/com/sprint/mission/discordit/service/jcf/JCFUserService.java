package service.jcf;

import entity.User;
import service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 인터페이스 구현제, implement를 사용해서 UserService에 적어놓은 기능을 수행한다고 약속
public class JCFUserService implements UserService {

    // List<User> : User만 들어갈 수 있는 리스트
    private final List<User> data;

    // 생성자. 데이터를 담을 수 있는 가방 생성. ArrayList<>() = 자동으로 늘어나는 가방
    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    // Override : 인터페이스에 적혀있는 기능을 그대로 가져와서 실제 동작을 채워 넣는다는 표시

    @Override
    public User createUser(String username, String email, String password) {
        User user = new User(username, email, password);
        data.add(user);
        return user;
    }

    @Override
    public User find(UUID id) {
        return data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public User updateUser(UUID id, String username, String email, String password) {
        User user = find(id);
        if (user != null) {
            user.update(username, email, password);
            return user;
        }
        return null;
    }

    @Override
    public void deleteUser(UUID id) {
        User user = find(id);
        if (user != null) data.remove(user);
    }
}