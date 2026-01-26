//실제 동작하는걸 담당하는 클래스 /세부레시피 쭉 적어놓은

package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> users = new ArrayList<>();
    //등록
    @Override
    public User create(String username, String mail, String phone) {
        User user = new User(username, mail, phone);
        users.add(user);
        return user;
    }
    //단건 조회
    @Override
    public User find(UUID id) {
        for(User user : users){
            if(user.getId().equals(id)){
                return user;
            }
        }
        return null;
    }

    //다건조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    //수정
    @Override
    public User updated(UUID id, String username, String email, String phone) {
        User user = find(id);
        if (user != null) {
            user.update(username, email, phone);
        }
        return user;
    }
    //삭제
    @Override
    public void delete(UUID id) {
        User user = find(id);
        if (user != null) {
            users.remove(user);
        }

    }
}