import entity.User;
import service.UserService;
import service.jcf.JCFUserService;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args){

        User user = new User("육선우", "ryuk6238@gmail.com", "2019010282");
        UserService UserService = new JCFUserService();

        boolean addFlag= userService.addUser(user);
        if(addFlag)
            System.out.println("추가 완료");
        else
            System.out.println("추가 실패");

        userService.getAllUser();
    }
}
