package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.ChannelType.PUBLIC;


public class JavaApplication {
    static void userCRUDTest(UserService userService) {
        System.out.println("--유저 생성 테스트--");
        // 생성
        User user = userService.create("서현하", "hhh@naver.com", "010-2222-2222");
        User user2 = userService.create("김춘자", "ccc@naver.com", "010-8888-8888");

        //User객체 user에다가 userService.create 메서드의 반환값으로 받은 유저객체를 담는다

        // 조회
        //userService안에 있는 find 메서드의 매개변수로 user.getId를 보낸다
        User foundUser = userService.find(user.getId());
        System.out.println();
        System.out.println("유저 조회(단건)");
        System.out.println("유저 아이디 : " + foundUser.getId());
        System.out.println("이름 : " + foundUser.getDisplayName());
        System.out.println("이메일 : " + foundUser.getEmail());
        System.out.println("휴대폰 번호 : " + foundUser.getPhoneNumber());
        System.out.println("생성 시간 : " + foundUser.getCreatedAt() + "\n");

        /* userService.find 메서드에 위에서 가져온 user객체의 id를 매개변수로 입력한다
        반환값으로 가져온 유저객체를 foundUser라는 변수명인 유저변수에 담는다
        그리고 foundUser가 가지고있는 id와 이름,이메일,전화번호,생성시간을 출력한다
        */
        List<User> foundUsers = userService.findAll();
        System.out.println("유저 조회 건수(다건) : " + foundUsers.size());
        for (int i = 0; i < foundUsers.size(); i++) {
            System.out.println("유저 아이디 : " + foundUsers.get(i).getId());
            //foundUsers는userService.findall 메소드를 통해서 받아온 전체유저가 담긴 리스트다
            System.out.println("이름 : " + foundUsers.get(i).getDisplayName());
            System.out.println("이메일 : " + foundUsers.get(i).getEmail());
            System.out.println("휴대폰 번호 : " + foundUsers.get(i).getPhoneNumber());
            System.out.println("생성 시간 : " + foundUsers.get(i).getCreatedAt() + "\n");

            /* data list를 foundUsers라는 유저객체를 담을 수 있는 list에 담는다
            foundUsers list의 크기를 출력한다(유저 조회건수)
            for문으로 foundUsers의 size만큼 반복문을 수행한다
            foundUsers의 i번째에 들어있는 user객체의 값을 출력한다
             */

        }// 수정
        userService.update(user.getId(), "이말자", "why@naver.com", "010-8282-8282");
        //userService에 있는 업데이트 메소드를 호출
        System.out.println();

        /* userService.delete메소드에 위에서 생성한 user 변수의 id값을 매개변수로 입력한다
         */


        // 삭제     <삭제할 유저의 이름을 출력만한다>
        System.out.println(user.getDisplayName() + " 유저를 삭제합니다" + "\n");
        userService.delete(user.getId());
                                   //매개변수 Id
        /* user service에 findall메소드를 통해서 유저삭제이후 list를 새로 반환받는다
        for문 안에서 유저삭제이후 list를 돌리면서 해당 list의 유저가 가지고있는 값을 출력한다
        */
        List<User> foundUsersAfterDelete = userService.findAll();

        System.out.println("유저 조회 건수: " + foundUsersAfterDelete.size());
        for (int i = 0; i < foundUsersAfterDelete.size(); i++) {
            System.out.println("유저 아이디 : " + foundUsersAfterDelete.get(i).getId());
            System.out.println("이름 : " + foundUsersAfterDelete.get(i).getDisplayName());
            System.out.println("이메일 : " + foundUsersAfterDelete.get(i).getEmail());
            System.out.println("휴대폰 번호 : " + foundUsersAfterDelete.get(i).getPhoneNumber());
            System.out.println("생성 시간 : " + foundUsersAfterDelete.get(i).getCreatedAt() + "\n");
        }  //get(i) i번째,

    }

    static void channelCRUDTest(ChannelService channelService) {
        // 생성
        /* 입력값으로 채널을 생성하고 새 채널을 만들어서 channel객체를 담는 리스트에
        넣어서 리턴값으로 채널을 반환해 다시 변수에 넣는다
        */
        System.out.println("-- 채널 생성 테스트 --");
        System.out.println();
        Channel channel = channelService.create("주의사항",PUBLIC);
        Channel channel2 = channelService.create("자유 게시판",PUBLIC);
        System.out.println();


        // 조회
        //생성한 채널중 첫번째로 생성한 채널의 객체를 가져와서 출력했다<foundChannel.getId()>
        Channel foundChannel = channelService.find(channel.getId());
        System.out.println("채널 조회(단건): " + foundChannel.getId());
        System.out.println("채널 이름 : " + channel.getDisplayName());
        System.out.println();

        // list<channel> 개체를 담아서 변수이름 foundChannels를 선언하고 findAll메소드를 이용
        List<Channel> foundChannels = channelService.findAll();
        System.out.println("채널 조회(다건): " + foundChannels.size());
        for (int i = 0; i < foundChannels.size(); i++) {
            System.out.println("채널 이름 : " + foundChannels.get(i).getDisplayName());

        }
        // int i를 0으로 선언하고 i가 파운드채널스 리스트의 크기보다 작으면 for문을 이용해서
        // foundChannels의 list값을get(i)
        // 수정
        System.out.println();
        System.out.println("수정 전 이름 : " + channel.getDisplayName());
        channelService.update(channel.getId(), "공지");
        System.out.println("수정 후 이름 : " + channel.getDisplayName());
        System.out.println("수정 시간 : " + channel.getUpdatedAt());

        // 삭제
        //삭제한 후에 남아있는 채널이 담긴 리스트에 삭제하고 싶은 리스트를 없앤 다음에 리스트 가져옴
        channelService.delete(channel.getId());
        List<Channel> foundChannelsAfterDelete = channelService.findAll();
        System.out.println(channel.getDisplayName() + " 채널을 삭제합니다" + "\n");
        System.out.println("\n채널 삭제 후 남은 목록");
        System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());

        for (int i = 0; i < foundChannelsAfterDelete.size(); i++) {
            System.out.println("남은 채널 아이디 : " + foundChannelsAfterDelete.get(i).getId());
            System.out.println("남은 이름 : " + foundChannelsAfterDelete.get(i).getDisplayName());
            System.out.println();
            System.out.println("---------------------------");
            System.out.println();
        }
    }

    static void messageCRUDTest(MessageService messageService) {
        // 생성
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        Message message = messageService.create("안녕하세요.", channelId, authorId);
        System.out.println("메시지 생성: " + message.getId());
        // 조회
        Message foundMessage = messageService.find(message.getId());
        System.out.println("메시지 조회(단건)\n메세지 아이디 : " + foundMessage.getId()+ "\n메세지 내용 : " + foundMessage.getContent() +"\n");
        List<Message> foundMessages = messageService.findAll();
        System.out.println("메시지 조회(다건): " + foundMessages.size());
        for(int i = 0; i < foundMessages.size(); i++) {
            System.out.println("메세지 내용 : " + foundMessages.get(i).getContent());
        }
        // 수정
        Message updatedMessage = messageService.update(message.getId(), "정말 힘들었습니다.");
        System.out.println("메시지 수정 : " + updatedMessage.getContent());
        // 삭제
        messageService.delete(message.getId());
        List<Message> foundMessagesAfterDelete = messageService.findAll();
        System.out.println("메시지 삭제 : " + foundMessagesAfterDelete.size());
    }

    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create("공지",ChannelType.PUBLIC);
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {

        // 서비스 초기화
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();
        UserService userService = new FileUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();
        MessageService messageService2 = new BasicMessageService(userRepository,channelRepository);


        //테스트
        userCRUDTest(userService);
        channelCRUDTest(channelService);
        messageCRUDTest(messageService);
        System.out.println();


        try {
            User user = userRepository.save(new User("woody", "woody@codeit.com", "1234"));
            Channel channel = channelRepository.save(new Channel("공지",PUBLIC));

            messageService.create("안녕하세요!", channel.getId(), user.getId());
            System.out.println("메시지 생성 성공!");

            UUID fakeId = UUID.randomUUID();
            messageService.create("가짜 유저의 메시지", channel.getId(), fakeId);

        } catch (NoSuchElementException e) {
            System.out.println("검증 실패 (정상): " + e.getMessage());
        }

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);
    }
}

