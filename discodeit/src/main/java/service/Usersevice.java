package service;

import entity.User;

import java.util.List;

public interface Usersevice {
//    생성
    User adduuser(User user);

//    조회
    User getuser(String userName);

//    전체 조회
    List getAllUser();

//    수정
    User updateUser(String name, String email, String number);

//    삭제
    boolean deleteUser(String userName);

}
