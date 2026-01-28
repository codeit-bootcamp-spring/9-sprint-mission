package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

public class JCFUserService implements UserService {

    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    // 1. 생성
    @Override
    public void join(User user) {
        data.add(user);
    }

    // 2. 단건 조회
    @Override
    public User findById(UUID id) {
        for (User user : data) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    // 2-1. 다건 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(data); // 복사본 반환
    }

    // 3. 수정
    @Override
    public boolean update(UUID id, String nickname, String phoneNumber, String password) {
        User found = findById(id);
        if (found == null) return false;

        // [개선] setter 직접 호출보다 도메인 update()로 묶어서 처리
        found.update(nickname, phoneNumber, password);
        return true;
    }

    // 4. 삭제
    @Override
    public boolean delete(UUID id) {
        User found = findById(id);
        if (found == null) return false;
        // remove(object) = 해당 객체를 리스트에서 제거, 성공 여부 boolean 반환
        return data.remove(found);
    }
}


