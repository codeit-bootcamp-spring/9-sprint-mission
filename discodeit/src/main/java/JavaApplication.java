import entity.Channel;
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


public class JavaApplication {
    static void userCRUDTest(UserService userService) {
        // 생성
        User user =userService.create("RYUK", "ryuk6238@gmail.com", "01066668888");
        System.out.println("==== 유저 등록 ====");
        System.out.println("유저 ID: " + user.getId());

        System.out.println();
        // 조회
        User foundUser = userService.find(user.getId());
        System.out.println("==== 유저 정보(단건) ====");
        System.out.println("유저 ID: " + foundUser.getId());
        System.out.println("이름: " + foundUser.getuserName());
        System.out.println("이메일: " + foundUser.getemail());
        System.out.println("전화번호: " + foundUser.getphoneNumber());

        System.out.println();
        //전체 조회
        List<User> foundUsers = userService.findAll();
        System.out.println("==== 유저 정보(다건) ====");
        System.out.println("유저 수: " + foundUsers.size());

        System.out.println();

        // 수정
        User updatedUser = userService.update(user.getId(), "WUK", "WUK@gmail.com", "01022228888");
        System.out.println("==== 유저 정보 수정 ====");
        System.out.println("이름: " + updatedUser.getuserName());
        System.out.println("이메일: " + updatedUser.getemail());
        System.out.println("전화번호: " + updatedUser.getphoneNumber());

        System.out.println();

        // 삭제
        userService.delete(user.getId());
        List<User> foundUsersAfterDelete = userService.findAll();
        System.out.println("==== 유저 삭제 ====");
        System.out.println("유저 수: " + foundUsersAfterDelete.size());
        System.out.println();
        }
        static void channelCRUDTest (ChannelService channelService){
            // 생성
            Channel channel = channelService.create("공지", "공지합니다", "이곳은 공지 채널입니다.");
            System.out.println("==== 채널 등록 ====");
            System.out.println("채널 ID: " + channel.getId());

            System.out.println();
            // 조회
            Channel foundChannel = channelService.find(channel.getId());
            System.out.println("==== 채널 정보(단건) ====");
            System.out.println("채널 ID: " + foundChannel.getId());
            System.out.println("채널 프레임: " + foundChannel.getFrame());
            System.out.println("채널 이름: " + foundChannel.getChannelName());
            System.out.println("메세지 내용: " + foundChannel.getDetail());

            System.out.println();
            //전체 조회
            List<Channel> foundChannels = channelService.findAll();
            System.out.println("==== 채널 정보(다건) ====");
            System.out.println("채널 수: " + foundChannels.size());

            System.out.println();

            // 수정
            Channel updatedChannel = channelService.update(channel.getId(), "게시판", "공지사항", "서류 제출 안내");
            System.out.println("==== 채널 정보 수정 ====");
            System.out.println("채널 ID: " + updatedChannel.getId());
            System.out.println("채널 프레임: " + updatedChannel.getFrame());
            System.out.println("채널 이름: " + updatedChannel.getChannelName());
            System.out.println("메세지 내용: " + updatedChannel.getDetail());

            System.out.println();

            // 삭제
            channelService.delete(channel.getId());
            List<Channel> foundChannelsAfterDelete = channelService.findAll();
            System.out.println("==== 채널 삭제 ====");
            System.out.println("채널 수: " + foundChannelsAfterDelete.size());
            System.out.println();
        }
        static void messageCRUDTest (MessageService messageService){
            // 생성
            UUID channelId = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();

            Message message = messageService.create("안녕하세요.", channelId, authorId);
            System.out.println("==== 메세지 등록 ====");
            System.out.println("메세지 ID: " + message.getId());

            System.out.println();

            // 조회
            Message foundMessage = messageService.find(message.getId());
            System.out.println("==== 메세지 조회(단건) ====");
            System.out.println("메시지 ID: " + foundMessage.getId());
            System.out.println("메시지 내용: " + foundMessage.getchat());


            System.out.println();

            // 전체 조회
            List<Message> foundMessages = messageService.findAll();
            System.out.println("==== 메세지 조회(다건) ====");
            System.out.println("메시지 수: " + foundMessages.size());

            System.out.println();

            // 수정
            Message updatedMessage = messageService.update(message.getId(), "반갑습니다.");
            System.out.println("==== 메세지 수정====");
            System.out.println("메시지 내용: " + updatedMessage.getchat());

            System.out.println();
            // 삭제
            messageService.delete(message.getId());
            List<Message> foundMessagesAfterDelete = messageService.findAll();
            System.out.println("==== 메세지 삭제 ====");
            System.out.println("메시지 수: " + foundMessagesAfterDelete.size());

        }
        public static void main (String[]args){
            // 서비스 초기화
            UserService userService = new JCFUserService();
            ChannelService ChannelService = new JCFChannelService();
            MessageService messageService = new JCFMessageService();

            // 테스트
            userCRUDTest(userService);
            channelCRUDTest(ChannelService);
            messageCRUDTest(messageService);
        }
}


