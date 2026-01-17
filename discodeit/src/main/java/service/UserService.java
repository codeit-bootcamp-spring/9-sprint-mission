package service;

import entity.User;
import java.util.List;
import java.util.UUID;

// [Interface]
// 목적: "기능의 목록(메뉴판)"만 정의한다.
// 특징: 실제 동작 코드(중괄호 {})가 없다.
public interface UserService {
    // 1. 생성
    void join(User user);
    // 2. 단건 조회
    User findById(UUID id);
    // 2-1. 다건 조회
    List<User> findAll();
    // 3. 수정
    boolean update(UUID id, String nickname, String phoneNumber, String password);
    // 4. 삭제
    boolean delete(UUID id);
}



