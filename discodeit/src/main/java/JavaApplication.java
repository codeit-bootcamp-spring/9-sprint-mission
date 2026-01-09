import Service.UserService;
import Service.jcf.JCFUserService;
import entity.User;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args)  {
        UserService userService = new JCFUserService();

        User user = userService.create(
                "윤성준",
                "jseong013@gmail.com",
                "010-6620-7432"
        );

        //등록 여부 확인
        if (userService.exitsById(user.getId())) {
            System.out.println("등록된 유저입니다.");
        }

        //전체 조회
        System.out.println("유저 수: " + userService.findAll().size());

        //단건 조회
        User foundUser = userService.findById(user.getId());
            if (foundUser != null) {
                System.out.println("조회 성공");
                System.out.println("이름: " + foundUser.getDisplayName());
                System.out.println("이메일: " + foundUser.getEmail());
                System.out.println("전화번호: " + foundUser.getPhoneNumber());
            } else {
                System.out.println("존재하지 않는 유저입니다.");
            }

        //삭제
        userService.delete(user.getId());

        //삭제여부
        System.out.println("삭제 후 등록 여부: " + userService.exitsById(user.getId()));
    }
}
