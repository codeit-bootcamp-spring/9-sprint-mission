package service;

import entity.User;
import java.util.List;

public interface Usersevice  {

    // 회원 생성
    User addUser(User user);

    // 전체 조회
    List<User> getAllUser();

    // 삭제
    void deleteUser(String userId);

    // 로그인
    User login(String identifier, String userNumber);

    // 회원 조회
    User getUserByUserId(String userId);
    User getUserByEmail(String email);
}
