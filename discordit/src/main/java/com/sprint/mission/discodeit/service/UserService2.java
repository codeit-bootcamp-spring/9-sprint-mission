package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService2 {

    User find(UUID id);

    List<User> findAll();

    void update(UUID id,String displayName, String email, String phoneNumber);

    boolean delete(UUID id);

    User create(String displayName, String email, String phoneNumber);
}


/* UserService라는  interface안에 메서드 빈상자를 만들고 상자의 이름이랑 입력값만 지정해둔 상태고
이 빈상자를 가지고 JCFUserService로 들고간다
 */