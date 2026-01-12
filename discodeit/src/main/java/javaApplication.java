import entity.User;
import exception.LoginFailedException;
import exception.UserNotFoundException;
import service.Usersevice;
import service.jcf.JCFUserService;

public class javaApplication {

    public static void main(String[] args) {

        Usersevice userService = new JCFUserService();

        userService.addUser(
                new User(
                        "hyeokjo",
                        "test1@test.com",
                        "혁조",
                        "U001"
                )
        );

        // 로그인
        try {
            userService.login("hyeokjo", "U001");
            System.out.println("로그인 성공");
        } catch (LoginFailedException e) {
            System.out.println(e.getMessage());
        }

        // 아이디 조회
        try {
            System.out.println(userService.getUserByUserId("hyeokjo"));
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // 이메일 조회
        try {
            System.out.println(userService.getUserByEmail("test1@test.com"));
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }

         // 수정

        }
    }

