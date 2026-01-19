package service;

import entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {


    //생성
    boolean addUser (User user);


    //조회
    User getUser(String displayName);
    User getUserById(String userId);
    //전체 조회
    List<User> getallUser();

    //수정


    User updateUser(String oldName, String newName, String email, String phoneNumber);

    //최건위 -> 전체 목록에서 검색 -> User 객체를 가쟈오고
    //-> 가져온 User 안에 필드인 id를 조회해서 리스트에서 검색한다음 삭제하도록 진행
    //삭제
    boolean deleteUser(String userName);


    //의존성
    User getbyId(UUID userId);

}
