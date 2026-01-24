
import entity.Channel;
import entity.ChannelType;
import entity.Message;
import entity.User;
import service.ChannelService;
import service.MessageService;
import service.UserService;
import service.jcf.JCFChannelService;
import service.jcf.JCFMessageService;
import service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;


// 테스트가 끝나면 User 정보가 담긴 리스트를 밖으로 반환. 외부에서 UserService를 받아온다.
public class JavaApplication {
    static List<User> userCRUDTest(UserService userService) {

        // 생성
        System.out.println("―――――――――――――――― ● 유저 ● ――――――――――――――――\n");

        User user = userService.createUser("woody", "woody@codeit.com", "woody1234");
        User user1 = userService.createUser("장현민", "jjjjj@codeit.com", "jjjjj1234");
        User user2 = userService.createUser("hyunmin", "hyunmin@codeit.com", "hyunmin1234");

        System.out.println("========= ● 신규 유저 ● =========");
        System.out.println("유저 아이디: " + user.getId());
        System.out.println("이름: " + user.getUsername());
        System.out.println("이메일: " + user.getEmail());
        System.out.println("비밀번호: " + user.getPassword());
        System.out.println("생성 시간: " + user.getCreatedAt());
        System.out.println("================================" + "\n");

        System.out.println("========= ● 신규 유저 ● =========");
        System.out.println("유저 아이디: " + user1.getId());
        System.out.println("이름: " + user1.getUsername());
        System.out.println("이메일: " + user1.getEmail());
        System.out.println("비밀번호: " + user1.getPassword());
        System.out.println("생성 시간: " + user.getCreatedAt());
        System.out.println("================================" + "\n");

        System.out.println("========= ● 신규 유저 ● =========");
        System.out.println("유저 아이디: " + user2.getId());
        System.out.println("이름: " + user2.getUsername());
        System.out.println("이메일: " + user2.getEmail());
        System.out.println("비밀번호: " + user2.getPassword());
        System.out.println("생성 시간: " + user.getCreatedAt());
        System.out.println("================================" + "\n");

        // 조회
        System.out.println("========= ● 유저 조회(단건) ● =========");
        User foundUser = userService.find(user.getId());
        System.out.println("유저 아이디: " + foundUser.getId());
        System.out.println("이름: " + foundUser.getUsername());
        System.out.println("이메일: " + foundUser.getEmail());
        System.out.println("비밀번호: " + foundUser.getPassword());
        List<User> foundUsers = userService.findAll();
        System.out.println("================================" + "\n");

        System.out.println("========= ● 유저 조회(다건) ● =========");
        System.out.println("유저 수: " + foundUsers.size());
        for (User u : userService.findAll()) {
            System.out.println("UserName: " + u.getUsername());
        }
        System.out.println("================================" + "\n");

        // 수정
        System.out.println("========= ● 유저 수정 ● =========");
        User updatedUser = userService.updateUser(user.getId(), "hoyohoyo", "hoyohoyo@codeit.com", "hoyohoyo1234");
        System.out.println("유저 아이디: " + updatedUser.getId());
        System.out.println("이름: " + updatedUser.getUsername());
        System.out.println("이메일: " + updatedUser.getEmail());
        System.out.println("비밀번호: " + updatedUser.getPassword());
        System.out.println("수정 시간: " + updatedUser.getUpdatedAt());
        System.out.println("================================" + "\n");

        System.out.println("========= ● 유저 수정 ● =========");
        User updatedUser1 = userService.updateUser(user1.getId(), "민현장", "mmmmm@codeit.com", "mmmmm1234");
        System.out.println("유저 아이디: " + updatedUser1.getId());
        System.out.println("이름: " + updatedUser1.getUsername());
        System.out.println("이메일: " + updatedUser1.getEmail());
        System.out.println("비밀번호: " + updatedUser1.getPassword());
        System.out.println("수정 시간: " + updatedUser1.getUpdatedAt());
        System.out.println("================================" + "\n");

        // 삭제
        System.out.println("========= ● 유저 삭제 ● =========");
        userService.deleteUser(user2.getId());
        System.out.println("삭제 아이디: " + user2.getId());
        User deletedUser = userService.find(user2.getId());
        if (deletedUser == null) {
            System.out.println("삭제 성공: 데이터 없음");
            System.out.println("삭제 시간: " + user.getUpdatedAt());
        } else {
            System.out.println("삭제 실패: 데이터 있음");
        }
        System.out.println("================================" + "\n");

        List<User> userList = userService.findAll();
        return userList;
    }

    static List<Channel> channelCRUDTest(ChannelService channelService) {
        // 생성
        System.out.println("―――――――――――――――― ★ 채널 ★ ――――――――――――――――\n");

        System.out.println("========= ★ 채널 생성 ★ =========");
        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        System.out.println("채널 생성: " + channel.getName());
        System.out.println("채널 설명: " + channel.getDescription());
        System.out.println("생성 시간: " + channel.getCreatedAt());
        System.out.println("================================" + "\n");

        System.out.println("========= ★ 채널 생성 ★ =========");
        Channel channel1 = channelService.create(ChannelType.PRIVATE, "자유1", "자유 1입니다.");
        System.out.println("채널 생성: " + channel1.getName());
        System.out.println("채널 설명: " + channel1.getDescription());
        System.out.println("생성 시간: " + channel1.getCreatedAt());
        System.out.println("================================" + "\n");

        System.out.println("========= ★ 채널 생성 ★ =========");
        Channel channel2 = channelService.create(ChannelType.PUBLIC, "자유2", "자유 2입니다.");
        System.out.println("채널 생성: " + channel2.getName());
        System.out.println("채널 설명: " + channel2.getDescription());
        System.out.println("생성 시간: " + channel2.getCreatedAt());
        System.out.println("================================" + "\n");

        // 조회
        System.out.println("========= ★ 채널 조회(단건) ★ =========");
        Channel foundChannel = channelService.find(channel.getId());
        System.out.println("채널 조회: " + foundChannel.getName());
        System.out.println("================================" + "\n");

        System.out.println("========= ★ 채널 조회(단건) ★ =========");
        Channel foundChannel1 = channelService.find(channel1.getId());
        System.out.println("채널 조회: " + foundChannel1.getName());
        System.out.println("================================" + "\n");

        System.out.println("========= ★ 채널 조회(다건) ★ =========");
        List<Channel> foundChannels = channelService.findAll();
        System.out.println("채널 개수: " + foundChannels.size());
        for (Channel c : channelService.findAll()) {
            System.out.println("채널 이름: " + c.getName());
        }
        System.out.println("================================" + "\n");

        // 수정
        System.out.println("========= ★ 채널 수정 ★ =========");
        System.out.println("채널 타입: " + channel.getType());
        System.out.println("채널 이름: " + channel.getName());
        System.out.println("채널 설명: " + channel.getDescription());
        System.out.println("↓ ↓ ↓ ↓ ↓");
        Channel updatedChannel = channelService.update(channel.getId(), "공지사항", "공지사항 채널입니다.");
        System.out.println("채널 타입: " + channel.getType());
        System.out.println("채널 이름: " + channel.getName());
        System.out.println("채널 설명: " + channel.getDescription());
        System.out.println("수정 시간: " + channel.getUpdatedAt());
        System.out.println("================================" + "\n");

        System.out.println("========= ★ 채널 수정 ★ =========");
        System.out.println("채널 타입: " + channel1.getType());
        System.out.println("채널 이름: " + channel1.getName());
        System.out.println("채널 설명: " + channel1.getDescription());
        System.out.println("↓ ↓ ↓ ↓ ↓");
        Channel updatedChannel1 = channelService.update(channel1.getId(), "자유채널1", "자유채널1 채널입니다.");
        System.out.println("채널 타입: " + channel1.getType());
        System.out.println("채널 이름: " + channel1.getName());
        System.out.println("채널 설명: " + channel1.getDescription());
        System.out.println("수정 시간: " + channel1.getUpdatedAt());
        System.out.println("================================" + "\n");

        // 삭제
        System.out.println("========= ★ 채널 삭제 ★ =========");
        channelService.delete(channel2.getId());
        System.out.println("삭제 번호: " + channel2.getId());
        Channel deletechannel = channelService.find(channel2.getId());
        if (deletechannel == null) {
            System.out.println("삭제 성공: 데이터 없음");
            System.out.println("삭제 시간: " + channel2.getUpdatedAt());
        } else {
                System.out.println("삭제 실패: 데이터 있음");
            }
        System.out.println("================================" + "\n");

        List<Channel> channelList = channelService.findAll();
        return channelList;
    }

    static void messageCRUDTest(MessageService messageService, List<User> userList, List<Channel> channelList) {
        // 생성

        System.out.println("―――――――――――――――― ■ 메시지 생성 ■ ――――――――――――――――\n");

        Channel realChannel = channelList.get(0);
        User realUser = userList.get(0);
        User realUser1 = userList.get(1);
        UUID authorUser = UUID.randomUUID();

        System.out.println("========= ■ 메시지 생성 ■ =========");
        Message message1 = messageService.create("나는 가짜입니다.", realChannel.getId(), authorUser);
        if (message1 != null) {
            System.out.println("메시지 생성 성공: " + message1.getContent());
        } else {
            System.out.println("메시지 생성 실패");
        }

        System.out.println("========= ■ 메시지 생성 ■ =========");
        Message message = messageService.create("안녕하세요.", realChannel.getId(), realUser.getId());
        if (message != null) {
            System.out.println("메시지 생성 성공: " + message.getContent());
        } else {
            System.out.println("메시지 생성 실패");
        }

        Message message2 = messageService.create("어디가세요?", realChannel.getId(), realUser1.getId());
        if (message2 != null) {
            System.out.println("메시지 생성 성공: " + message2.getContent());
        } else {
            System.out.println("메시지 생성 실패");
        }

        Message message3 = messageService.create("코드잇가요", realChannel.getId(), realUser.getId());
        if (message3 != null) {
            System.out.println("메시지 생성 성공: " + message3.getContent());
        } else {
            System.out.println("메시지 생성 실패");
        }
        System.out.println("=================================" + "\n");


        // 조회
        System.out.println("========= ■ 메시지 조회(단건) ■ =========");
        Message foundMessage = messageService.find(message.getId());
        System.out.println("조회 메시지: " + foundMessage.getContent());
        System.out.println("=================================" + "\n");

        System.out.println("========= ■ 메시지 조회(다건) ■ =========");
        List<Message> foundMessages = messageService.findAll();
        System.out.println("메시지 개수: " + foundMessages.size());
        System.out.println("=================================" + "\n");

        // 수정
        System.out.println("========= ■ 메시지 수정 ■ =========");
        System.out.println("기존 메시지: " + message.getContent());
        System.out.println("↓ ↓ ↓ ↓ ↓");
        Message updatedMessage = messageService.update(message.getId(), "반갑습니다.");
        System.out.println("수정 메시지: " + updatedMessage.getContent());
        System.out.println("수정 시간: " + updatedMessage.getUpdatedAt());
        System.out.println("=================================" + "\n");

        // 삭제
        System.out.println("========= ■ 메시지 삭제 ■ =========");
        messageService.delete(message.getId());
        System.out.println("삭제 메시지: " + message.getContent());
        Message deletemessage = messageService.find(message.getId());

        //메시지를 조회했을 때 없다면 삭제 성공
        if (deletemessage == null) {
            System.out.println("삭제 성공: 데이터 없음");
            System.out.println("삭제 시간: " + updatedMessage.getUpdatedAt());
        } else {
            System.out.println("삭제 실패: 데이터 있음");
        }
        System.out.println("=================================" + "\n");
    }

    public static void main(String[] args) {
        // 서비스 초기화
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);

        // 테스트
        List<User> users = userCRUDTest(userService);
        List<Channel> channels = channelCRUDTest(channelService);
        messageCRUDTest(messageService, users, channels);

    }
}