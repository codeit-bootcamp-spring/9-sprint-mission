package service;

import entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    //생성
    User addUser(String displayName, String email, String phoneNumber);

    //조회
    User getUser(UUID id);

    //전체조회
    List<User> getAllUser();

    //수정

    void updateUser(UUID id, String displayName, String email, String phoneNumber);

    //삭제
    //전체목록 검색 -> User객체 가져오기 -> 가져온 User 객체 안에서 id 조회 -> 리스트에서 검색 후 삭제
    //삭제가 되었는지 체크하는 것이 중요
    void deleteUser(UUID id);
}
