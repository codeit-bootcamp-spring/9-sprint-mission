package service.jcf;


import entity.User;
import service.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;


public class JCFUserService implements UserService {

    public final List<User> users;
    //private  final Set<UserID> ids;


    public JCFUserService() {
        this.users = new ArrayList<>();
        users.add(new User("YUK","YUK@email.com","01022790657"));
        users.add(new User("KIM","KIM@email.com","01011112222"));
        users.add(new User("PARK","PARK@email.com","01022223333"));
        users.add(new User("CHOI","CHOI@email.com","01033334444"));
        users.add(new User("HUANG","HUANG@email.com","01044445555"));
    }
    @Override
    public User find(UUID id) {
        for (User user : users) {
            if(user.getId().equals(id)){
                return user;
            }
        }
        return null;
    }

    @Override
    public User findByName(String userName) {
        for (User user : users) {
            if (user.getuserName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User create(String userName, String email, String phoneNumber) {
        User user = new User(userName, email, phoneNumber);
        users.add(user);
        return user;
    }

    @Override
    public User update(UUID id, String userName, String email, String phoneNumber) {
        for(User user : users) {
            if (user.getId().equals(id)) {

                user.setuserName(userName);
                user.setemail(email);
                user.setphoneNumber(phoneNumber);

                return user;
            }
        }
        return null;
    } // 다시 공부

    @Override
    public List<User> findAll() { return new ArrayList<>(users); }

    @Override
    public List<User> checkAll() {
        return users.stream() // -> 스트림 시작
                .sorted(Comparator.comparing(User :: getuserName))
                // sorted =  ...기준으로 정렬 -> 정렬 기준 필요 ->  Comparator.comparing(User :: getuserName
                // compare = 두 객체를 비교하는 규칙
                // comparing = 기준을 정해서 규칙을 만들어줌
                //(User :: getuserName) -->  같음 / user -> user.geruserName():
                .toList();
    }

    @Override
    public void delete(UUID id) {
        users.removeIf(user -> user.getId().equals(id));
    }
}




