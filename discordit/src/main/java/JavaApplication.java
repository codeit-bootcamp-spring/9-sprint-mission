import entity.User;
import service.UserService;
import service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        //JCFUserService 테스트 해보세요~~
        UserService userService = new JCFUserService();

        //User객체 활용
        User user1 = new User("임혜민", "ellen@gmail.com","010-1111-1111");
        User user2 = new User("홍길동", "david@gmail.com","010-2222-2222");

        userService.addUser(user1);
        userService.addUser(user2);


        //유저 등록 확인

        boolean addFlag = userService.addUser(user1);
        if(addFlag)
            System.out.println("========== <<추가 완료>> ==========");
        else
            System.out.println("========== <<추가 실패>> ==========");

        boolean addFlag2 = userService.addUser(user2);
        if(addFlag2)
            System.out.println("========== <<추가 완료>> ==========");
        else
            System.out.println("========== <<추가 실패>> ==========");





        System.out.println(userService.getAllUser());


        //수정 및 조회
        userService.updateUser(
                user1.getId(),
                "임혜민수정",
                "ELLEN@gmail.com",
                "010-3333-3333"
        );
        System.out.println("===== 수정된 유저(USER) 정보 =====");

        //게터함수 활용하기!!
        System.out.println(userService.getUser(user1.getId()));


        //





    }
}
