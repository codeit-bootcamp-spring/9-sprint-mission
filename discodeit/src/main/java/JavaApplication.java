import entity.Channel;
import entity.User;
import service.ChannelService;
import service.UserService;
import service.jcf.JCFChannelService;
import service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        // 1. 생성
        System.out.println("=============== 회원 가입 ================");
        User user1 = new User("qweqwe", "1111", "임코딩", "010-1111-1111", "멸살죽");
        User user2 = new User("asdasd", "2222", "김치국", "010-2222-2222", "불꽃남자");
        User user3 = new User("zxczxc", "3333", "기신이", "010-3333-3333", "귀신이 고칼로리");
        User user4 = new User("xcvxcv", "4444", "홍금자", "010-4444-4444", "83세 홍금자 할머니의 마지막 카프킥");

        userService.join(user1);
        System.out.println(user1.getUsername() +"님 가입을 환영합니다.");
        userService.join(user2);
        System.out.println(user2.getUsername() +"님 가입을 환영합니다.");
        userService.join(user3);
        System.out.println(user3.getUsername() +"님 가입을 환영합니다.");
        userService.join(user4);
        System.out.println(user4.getUsername() +"님 가입을 환영합니다.");

        // 2. 단건 조회
        System.out.println("=============== 단건 조회 ================");
        User foundUser = userService.findById(user4.getId());

        if (foundUser != null) {
            System.out.println("| 아이디 : " + foundUser.getLoginId() + "                        |");
            System.out.println("| 비밀번호 : " + foundUser.getPassword() + "                        |");
            System.out.println("| 이름 : " + foundUser.getUsername() + "                           |");
            System.out.println("| 닉네임 : " + foundUser.getNickname() + " |");
            System.out.println("| 휴대번호 : " + foundUser.getPhoneNumber() + "               |");
        }

        // 2-1. 전체 조회
        System.out.println("=============== 전체 조회 ================");
        List<User> users = userService.findAll();

        for (User u : users) {
            System.out.println("아이디: " + u.getLoginId()
                    + " | 비밀번호: " + u.getPassword()
                    + " | 이름 " + u.getUsername()
                    + " | 닉네임: " + u.getNickname()
                    + " | 휴대번호: " + u.getPhoneNumber());
        }
        // 3. 수정
        System.out.println("================ 변경 사항 =================");
        System.out.println("기존 닉네임: " + user1.getNickname()); //Before
        user1.setNickname("3년차 같은 중고신입"); // Change
        userService.update(user1.getId(),user1.getNickname(),user1.getPhoneNumber(),user1.getPassword()); // Update
        System.out.println("    ↓");
        System.out.println("변경된 닉네임: " + user1.getNickname());

        // 4. 삭제
        System.out.println("================ 삭제 =================");
        System.out.println("삭제 대상: " + user2.getUsername());

        boolean deleted = userService.delete(user2.getId());
        System.out.println("삭제 성공 여부: " + deleted);

        ChannelService channelService = new JCFChannelService();

        // 1. 생성
        System.out.println("=============== 채널 생성 ================");
        Channel channel1 = new Channel("종겜방", "연애하지마십쇼", false);
        channelService.create(channel1);
        System.out.println(channel1.getChannelName() +" 채널이 생성되었습니다.");

        // 2. 조회
        System.out.println("=============== 채널 조회 ================");
        Channel foundChannel = channelService.findByName(channel1.getChannelName());
        System.out.println("채널명 : " + foundChannel.getChannelName());
        System.out.println("채널 소개: " + foundChannel.getChannelDescription());

        // 3. 수정
        System.out.println("=============== 채널명 수정 ================");
        channelService .update(channel1.getId(), "서울런닝크루","연애하지마십쇼",true);
        System.out.println("채널명: " + channel1.getChannelName());
        System.out.println("채널 소개: " + channel1.getChannelDescription());

        // 4. 삭제
        System.out.println("=============== 채널 삭제 ================");
        channelService.delete(channel1.getId());
        if (channelService.findByName(channel1.getChannelName()) != null ) {
            System.out.println((channel1.getChannelName()) + ": 채널 삭제 실패");
        }  else {
            System.out.println((channel1.getChannelName()) + ": 채널 삭제 완료");
        }
    }
}
