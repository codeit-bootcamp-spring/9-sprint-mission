package service;


import entity.User;
import java.util.List;


public interface UserService{
    //조회
    void getUser(String userName);


    // 등록
    User addUser(String userName, String email, String phoneNumber);

    //전체 조회

    List<User> getAllUser();


        // 수정
    User updateUser (String userName, String email, String phoneNumber);


    // 이름으로 전체 목록에서 검색 User 객체에서 가져오고
    // 가져온 User안에 필드인 id를 조회해서 리스트에서 검색한다음 삭제하도록 진행
    // 삭제
    void deleteUser (User userName);


}

