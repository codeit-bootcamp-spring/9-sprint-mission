//유저 관련 기능을 정의하는 인터페이스 /레시피

package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {

    //등록
    User create(String username, String mail, String phone);

    //조회(단건)
    User find(UUID id);

    //(다건)
    List<User> findAll();

    //수정
    User updated(UUID id, String username, String email, String phone);

    //삭제
    void delete(UUID id);

}