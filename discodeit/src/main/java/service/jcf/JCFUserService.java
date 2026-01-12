package service.jcf;


import entity.User;
import service.UserService;

import java.util.ArrayList;
import java.util.List;

public class JCFUserService implements UserService {

    private  final List<User> userList = new ArrayList<>();
    private String userName;

    public JCFUserService() {
        super();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }


    @Override
    public void getUser(String userName) {
        boolean found = false;
        for (User user : userList) {
            if(user.getUserName().equals(userName)){
                System.out.println("\n[검색 결과]");
                System.out.println("ID: " + user.getId());
                System.out.println("이름: " + user.getUserName());
                System.out.println("메일: " + user.getEmail());
                System.out.println("번호: " + user.getPhoneNumber());
                found = true;
                break; // 찾았으므로 반복 종료
            }
        }
        if (! found) {
            System.out.println("찾을 수 없습니다.");
        }

    }




    public User addUser(String userName, String email, String phoneNumber) {
        userList.add(new User(userName, email, phoneNumber));
        return null;
    }

    public User addUser() {
        return null;
    }

    @Override
    public List<User> getAllUser() {
        return List.of();
    }

    @Override
    public User updateUser(String displayName, String email, String phoneNumber) {
        return null;
    }

    @Override
    public void deleteUser(User displayName) {
        userList.removeIf(user -> user.getUserName().equals(userName));
    }
}

