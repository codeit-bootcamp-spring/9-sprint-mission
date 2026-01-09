package Service.jcf;

import Service.UserService;
import entity.User;

import java.util.*;

public class JCFUserService implements UserService {
    //JCF 기반 저장소
    private final Map<UUID, User> data = new HashMap<>();

    //생성
    @Override
    public User create(String displayName, String email, String phoneNumber) {
       User user = new User(displayName, email, phoneNumber);
       data.put(user.getId(), user);
       return user;
    }

    //수정
    @Override
    public User update(UUID userId, String displayName, String email, String phoneNumber) {
        User user = data.get(userId);
        if (user == null) {
            return null;  //또는 예외
        }
        user.update(displayName, email, phoneNumber); //user 클래스에서 업데이트 메소드
        return user;
    }

    //건별찾기
    @Override
    public User findById(UUID userid) {
        return data.get(userid);
    }

    //전체찾기
    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    //삭제
    @Override
    public void delete(UUID userId) {
        data.remove(userId);
    }

    //등록여부 확인
    @Override
    public boolean exitsById(UUID userId) {
        return data.containsKey(userId);
    }
}
