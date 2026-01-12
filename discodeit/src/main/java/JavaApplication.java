import entity.Channel;
import entity.User;
import service.UserService;
import service.jcf.JCFUserService;

import java.util.Scanner;


public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        Scanner sc = new Scanner(System.in);


        userService.addUser("육선우", "ryuk6238@gmail.com", "01022790657");
        userService.addUser("김철수", "kims@gmail.com", "01012345678");
        userService.addUser("황건적", "huangs@gmail.com", "01001010101");
        Channel channel = new Channel("1번방");

        String searchName = sc.nextLine();
        System.out.println(searchName);
    }}