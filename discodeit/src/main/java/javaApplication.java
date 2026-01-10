import entity.User;

import java.util.ArrayList;
import java.util.List;

public class javaApplication {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();

        //  유저 생성
        User user1 = new User("test1@test.com", "혁조", "010-1111-2222");
        User user2 = new User("test2@test.com", "민수", "010-3333-4444");

        //  리스트에 추가
        users.add(user1);
        users.add(user2);

        //  유저 내용 출력
        for (User user : users) {
            System.out.println(user);


        }
    }
}
