import entity.Channel;
import entity.ChannelType;
import entity.Message;
import entity.User;
import repository.ChannelRepository;
import repository.MessageRepository;
import repository.UserRepository;
import repository.file.FileChannelRepository;
import repository.file.FileMessageRepository;
import repository.file.FileUserRepository;
import repository.jcf.JCFChannelRepository;
import repository.jcf.JCFMessageRepository;
import repository.jcf.JCFUserRepository;
import service.Basic.BasicChannelService;
import service.Basic.BasicMessageService;
import service.Basic.BasicUserService;
import service.ChannelService;
import service.MessageService;
import service.UserService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {
    static void userCRUDTest(UserService userService) {
        // 생성
        User user = userService.create("woody", "01019427577", "woody1234@codeit.com");
        System.out.println("유저 생성: " + user.getId());
        // 조회
        User foundUser = userService.findByID(user.getId());
        System.out.println("유저 조회(단건): " + foundUser.toString());
        List<User> foundUsers = userService.getAll();
        System.out.println("유저 조회(다건): " + foundUsers.size());
        // 수정
        User updatedUser = userService.update(user.getId(), null, "01087749923", "woody5678@codeit.com");
        System.out.println("유저 수정: " + String.join("/", updatedUser.toString()));
        // 삭제
        userService.remove(user.getId());
        List<User> foundUsersAfterDelete = userService.getAll();
        System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
    }

    static void channelCRUDTest(ChannelService channelService) {
        // 생성
        Channel channel = channelService.create(ChannelType.PUBLIC, "공지");
        System.out.println("채널 생성: " + channel.getId());
        // 조회
        Channel foundChannel = channelService.findByID(channel.getId());
        System.out.println("채널 조회(단건): " + foundChannel.toString());
        List<Channel> foundChannels = channelService.getAll();
        System.out.println("채널 조회(다건): " + foundChannels.size());
        // 수정
        Channel updatedChannel = channelService.updateName(channel.getId(), "공지사항");
        System.out.println("채널 수정: " + String.join("/", updatedChannel.toString()));
        // 삭제
        channelService.remove(channel.getId());
        List<Channel> foundChannelsAfterDelete = channelService.getAll();
        System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());
    }

    static void messageCRUDTest(MessageService messageService) {
        // 생성
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        Message message = messageService.create(channelId, authorId, "안녕하세요.");
        System.out.println("메시지 생성: " + message.getId());
        // 조회
        Message foundMessage = messageService.findByID(message.getId());
        System.out.println("메시지 조회(단건): " + foundMessage.toString());
        List<Message> foundMessages = messageService.getAll();
        System.out.println("메시지 조회(다건): " + foundMessages.size());
        // 수정
        Message updatedMessage = messageService.updateContent(message.getId(), "반갑습니다.");
        System.out.println("메시지 수정: " + String.join("/", updatedMessage.toString()));
        // 삭재
        messageService.remove(message.getId());
        List<Message> foundMessagesAfterDelete = messageService.getAll();
        System.out.println("메시지 삭제: " + foundMessagesAfterDelete.size());
    }

    static User setupUser(UserService userService) {
        return userService.create("woody", "woody@codeit.com", "woody1234");
    }

    static Channel setupChannel(ChannelService channelService) {
        return channelService.create(ChannelType.PUBLIC, "공지");
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create(channel.getId(), author.getId(), "안녕하세요.");
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {

//        // File
//        System.out.println("\n***** File Repository Test *****\n");
//        UserRepository userRepository = new FileUserRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();

        // JCF
        System.out.println("\n***** JCF Repository Test *****\n");
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        // 서비스 초기화
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository);

        // 테스트
        userCRUDTest(userService);
        channelCRUDTest(channelService);
        messageCRUDTest(messageService);

//        // 셋업
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//        // 테스트
//        messageCreateTest(messageService, channel, user);
    }
}

