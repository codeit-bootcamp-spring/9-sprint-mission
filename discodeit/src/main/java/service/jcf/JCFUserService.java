package service.jcf;


import entity.User;
import service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class JCFUserService implements UserService {

    private  final List<User> users;


    public JCFUserService() {
       this.users = new ArrayList<>();

        // 기본적으로 모든 클래스는 Object의 상속을 받는다(Object
    }

   /* public User addUser(String userName, String email, String phoneNumber) {
        users.add(new User(userName, email, phoneNumber));
        return null;
    }
    public User addUser() {
        return null;
    }*/

    @Override
    public User find(UUID id) {
        for (User user : users) {
            if(user.getId().equals(id)){
                return user;
            }
        }
        return null;
        // 람다? return users.stream().filter(user -> user.getId().equals(id))
        //      .findFirst() 첫번째 검색 결과를 반환
        //      .orElse(null);  아니면 null
    }

    @Override
    public User create(String userName, String email, String phoneNumber) {
        User user = new User(userName, email, phoneNumber);
        users.add(user);
        return user;
    }


    @Override
    public User update(UUID id, String userName, String email, String phoneNumber) {

    /*{   User user = find(id);
        if (user != null) user.update(userName, email, phoneNumber);
        return user;*/

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
    public void delete(UUID id) {
        users.removeIf(user -> user.getId().equals(id));
    }
}

/*      stream 다시

    return users.stream() /<- 시작
     람다 -> .filter(user -> user.getId().equals(id)) /<- 필터 : filter 말고 다른 용어?변수?

            .findFirst() /<- 첫번째 값 채용?획득? 다른
            .orElse(null); /<-예외

    toString => 코드값으로 변환된 데이터를 문자열로 출력?




 */





