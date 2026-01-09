import entity.User;
import service.UserService;
import service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        //JCFUserService 테스트 해보세요~~

        //User객체 활용
        User user = new User("임혜민", "ellen@naver.com","010-1234-5678");
        UserService userService = new JCFUserService();

        //userService.addUser(user);

        boolean addFlag = userService.addUser(user);
        if(addFlag)
            System.out.println("========== <<추가 완료>> ==========");
        else
            System.out.println("========== <<추가 실패>> ==========");

        //userService.getAllUser();
        System.out.println("유저 목록 : "+ userService.getAllUser());








    }
}
