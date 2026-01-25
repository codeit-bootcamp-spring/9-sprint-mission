import entity.Channel;
import entity.User;
import entity.Message;

import service.ChannelService;
import service.MessageService;
import service.UserService;

import service.jcf.JCFUserService;
import service.jcf.JCFChannelService;
import service.jcf.JCFMessageService;

import java.util.List;
import java.util.UUID;


public class JavaApplication {
    static void userCRUDTest(UserService userService) {

        User user1 = userService.addUser("임혜민", "ellen@gmail.com","010-1111-1111");
        User user2 = userService.addUser("홍길동", "david@gmail.com","010-2222-2222");

        //중복 확인
//        User user3 = userService.addUser("임혜민", "ellen@naver.com","010-4444-4444");
//        System.out.println(user3);

        //유저 생성 CREATE
        /*
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
*/
        System.out.println("=======  (CREATE)유저 생성   =======");
        System.out.println("유저 생성: " + user1.getId());
        System.out.println("유저 생성: " + user2.getId());

        //중복확인 확인하기

        System.out.println("=======  (CREATE)중복 유저   =======");
        try {
            User user3 = userService.addUser("임혜민", "ellen@naver.com","010-4444-4444");
        }
        catch (IllegalStateException e){
            System.out.println();
            System.out.println(e.getMessage());

        }

        //System.out.println("유저 생성: " + user3.getId());

        //유저 조회 READ, user2에 대한 정보를 조회한다.
        User foundUser = userService.getUser(user2.getId());
        System.out.println("======= (READ)유저2 정보 조회 =======");
        System.out.println("유저 ID : "+ foundUser.getId());
        System.out.println("개인유저 정보 조회: \n"+ foundUser);

        //유저 전체목록 조회 READ ALL
        System.out.println("======= (READ)전체유저 조회  =======");
        List<User> foundUsers = userService.getAllUser();
        for (User user : foundUsers) {
            System.out.println(user);
        }

        //System.out.println(userService.getAllUser());
        System.out.println("전체 유저수 : " + foundUsers.size() );

        //수정 및 조회
        userService.updateUser(
                user1.getId(),
                "임혜민수정",
                "ELLEN@gmail.com",
                "010-3333-3333"
        );
        System.out.println("======= (UPDATE)유저 수정  =======");
        System.out.println(userService.getUser(user1.getId()));

        //유저 삭제 DELETE
        userService.deleteUser(user2.getId());
        List<User> removeUser  = userService.getAllUser();
        System.out.println("======= (REMAIN)남은 유저  =======");
        System.out.println(removeUser);
        System.out.println("남은 유저수 : " + removeUser.size());


    }

    static void channelCRUDTest(ChannelService channelService){

        //채널 생성 CREATE
        //ChannelType.PUBLIC(채널의 공개여부) <<enum 활용하기 지피티 활용
        Channel channel1 = channelService.addChannel(Channel.ChannelType.PUBLIC,"공지", "공지 채널입니다.");
        Channel channel2 = channelService.addChannel(Channel.ChannelType.PRIVATE,"2팀 단체방", "코드잇 2팀 소통방입니다!");

        System.out.println("=======  (CREATE)채널 생성   =======");
        System.out.println("채널 생성: " + channel1.getId());
        System.out.println("채널 생성: " + channel2.getId());

        //채널 조회 READ
        Channel foundChannel = channelService.getChannel(channel1.getId());
        System.out.println("=======  (READ)채널_1 조회   =======");
        System.out.println("채널 ID : " + foundChannel.getId());
        System.out.println("개인유저 정보 조회: \n"+ foundChannel);

        //채널 전체 조회 READ
        System.out.println("=======  (READ)전체채널 조회  =======");
        List<Channel> foundChannels = channelService.getAllChannel();
        for (Channel channel : foundChannels) {
            System.out.println(channel);
        }
        //System.out.println(channelService.getAllChannel());
        System.out.println("전체 채널수 : " + foundChannels.size());

        //채널 수정 UPDATE
        Channel updatedChannel = channelService.updateChannel(
                channel1.getId(),
                "공지방",
                "이곳은 공지를 작성하는 방입니다."
        );
        System.out.println("======= (UPDATE)채널 수정  =======");
//        System.out.println(updatedChannel.getChannelName() +"\n"+ updatedChannel.getDescription());
        System.out.println(channelService.getChannel(channel1.getId()));

        //채널 삭제 DELETE
        channelService.deleteChannel(channel1.getId());
        List<Channel> removeChannel  = channelService.getAllChannel();
        System.out.println("======= (REMAIN)남은 채널  =======");
        System.out.println(removeChannel);
        System.out.println("남은 채널수 : " + removeChannel.size());



    }

    static void messageCRUDTest(MessageService messageService) {
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        //메세지 생성
        Message message1 = messageService.addMessage("저는 임헤민입니다.", channelId, authorId);
        Message message2 = messageService.addMessage("Hello, my name is Ellen", channelId, authorId);
        System.out.println("=======  (CREATE)메세지 생성  =======");
        System.out.println("메세지 생성: " + message1.getId());
        System.out.println("메세지 생성: " + message2.getId());

        //단건 조회
        //  message변수의 id를 getID메소드를 통해 불러와서 그 값으로
        //  getMessage메소드에서 해당 ID값에 해당하는 메세지 정보를 찾아서 foundMessage에 저장
        Message foundMessage = messageService.getMessage(message2.getId());
        System.out.println("=======  (READ)메시지 조회   =======");
        System.out.println("메세지 ID : " + foundMessage.getId());
        System.out.println("개인유저 정보 조회: \n"+ foundMessage);

        //다건 조회
        System.out.println("=======  (READ)전체 메세지   =======");
        List <Message> messages = messageService.getAllMessage();
        System.out.println(messageService.getAllMessage());
        System.out.println("전체 메세지수 : " + messages.size());

        //메세지 수정
        messageService.updateMessage(message1.getId(), "다들 반갑습니다!!!");
        System.out.println("======= (UPDATE)메세지 수정  =======");
        System.out.println(messageService.getMessage(message1.getId()));


        //메세지 삭제
        messageService.deleteMessage(message2.getId());
        List<Message> removeMessage  = messageService.getAllMessage();
        System.out.println("======= (REMAIN)남은 메세지  =======");
        System.out.println(removeMessage);
        System.out.println("남은 메세지수 : " + removeMessage.size());

    }

    public static void main(String[] args) {
        // 서비스 초기화
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService,channelService);

        // 테스트
        userCRUDTest(userService);
        channelCRUDTest(channelService);
        messageCRUDTest(messageService);
    }

}
