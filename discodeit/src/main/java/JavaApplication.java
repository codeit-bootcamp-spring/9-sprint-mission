import entity.User;
import service.UserService;
import service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.List;


public class JavaApplication {
    public static void main(String[] args) {
        User user = new User("전승현","asdf@gmail.com","01212301230");
        UserService service = new JCFUserService();


        service.addUser(user);
        System.out.println("멤버 조회 : " + service.getUser(user.getUsername()));










    }
}
