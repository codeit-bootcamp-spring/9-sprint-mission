//import com.sprint.mission.discodeit.DTO.UserDTO;
//import com.sprint.mission.discodeit.entity.ChannelType;
//import com.sprint.mission.discodeit.entity.Message;
//
//import java.util.List;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
//import com.sprint.mission.discodeit.service.basic.BasicUserService;
//import com.sprint.mission.discodeit.entity.Channel;
//
//import java.util.UUID;
//
//public class NewJavaApplication {
//    static void userCRUDTest(UserService userService) {
//        // 생성
//        User user = userService.createUser("d","df","");
//        System.out.println("유저 생성: " + user.getUserId());
//        // 조회
//        User foundUser = userService.findByUserId(user.getUserId());
//        System.out.println("유저 조회(단건): " + foundUser.getUserId());
//        List<User> foundUsers = userService.findAllUser();
//        System.out.println("유저 조회(다건): " + foundUsers.size());
//        // 수정
//        User updatedUser = userService.updateUser(user.getUserId(), null, null, "woody5678");
//        System.out.println("유저 수정: " + String.join("/", updatedUser.getUserName(), updatedUser.getEmail(), updatedUser.getPassword()));
//        // 삭제
//        userService.deleteUser(user.getUserId());
//        List<User> foundUsersAfterDelete = userService.findAllUser();
//        System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
//    }
//
//    static void channelCRUDTest(ChannelService channelService) {
//        // 생성
//        Channel channel = channelService.createChannel(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
//        System.out.println("채널 생성: " + channel.getChannelId());
//        // 조회
//        Channel foundChannel = channelService.findByChannelId(channel.getChannelId());
//        System.out.println("채널 조회(단건): " + foundChannel.getChannelId());
//        List<Channel> foundChannels = channelService.findAllChannel();
//        System.out.println("채널 조회(다건): " + foundChannels.size());
//        // 수정
//        Channel updatedChannel = channelService.updateChannel(channel.getChannelId(), "공지사항", null, null);
//        System.out.println("채널 수정: " + String.join("/", updatedChannel.getChannelName(), updatedChannel.getDescription()));
//        // 삭제
////        channelService.deleteChannel(channel.getChannelId());
////        List<Channel> foundChannelsAfterDelete = channelService.findAllChannel();
////        System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());
//    }
//
//    static void messageCRUDTest(MessageService messageService) {
//        // 생성
//        UUID channelId = UUID.randomUUID();
//        UUID authorId = UUID.randomUUID();
//        Message message = messageService.createMessage("안녕하세요.", channelId, authorId);
//        System.out.println("메시지 생성: " + message.getMessageId());
//        // 조회
//        Message foundMessage = messageService.findByMessageId(message.getMessageId());
//        System.out.println("메시지 조회(단건): " + foundMessage.getMessageId());
//        List<Message> foundMessages = messageService.findAllMessage();
//        System.out.println("메시지 조회(다건): " + foundMessages.size());
//        // 수정
//        Message updatedMessage = messageService.updateMessage(message.getMessageId(), "반갑습니다.");
//        System.out.println("메시지 수정: " + updatedMessage.getMessageText());
//        // 삭재
////        messageService.deleteMessage(message.getMessageId());
////        List<Message> foundMessagesAfterDelete = messageService.findAllMessage();
////        System.out.println("메시지 삭제: " + foundMessagesAfterDelete.size());
//    }
//    static void sendMessage(UserService userService, ChannelService channelService, MessageService messageService) {
//        Channel channel = channelService.createChannel(
//                ChannelType.PUBLIC,
//                "자유방",
//                "맘껏 떠드세요"
//        );
//
//
////        User user1 = userService.createUser(
////                "용땡땡",
////                "abc@email.com",
////                "01012345678"
////        );
////
////        User user2 = userService.createUser(
////                "강땡땡",
////                "def@email.com",
////                "01092739271"
////        );
////
////        Message message1 = messageService.createMessage("ㅎㅇ", channel.getChannelId(), user1.getUserId());
////        System.out.println("유저1 \"" + user1.getUserName() + "\"이(가) 메세지를 보냈습니다 :" + message1.getMessageText());
////
////        Message message2 = messageService.createMessage("안냐세여", channel.getChannelId(), user2.getUserId());
////        System.out.println("유저2 \"" + user2.getUserName() + "\"이(가) 메세지를 보냈습니다 :" + message2.getMessageText());
//    }
//
//
//        static User setupUser(UserService userService) {
//        User user = userService.createUser("woody", "woody@codeit.com", "821031039281");
//        System.out.println(user.toString());
//        return user;
//    }
//
//    static Channel setupChannel(ChannelService channelService) {
//        Channel channel = channelService.createChannel(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
//        System.out.println(channel.toString());
//        return channel;
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        Message message = messageService.createMessage("안녕하세요.", channel.getChannelId(), author.getUserId());
//        System.out.println("메시지 생성: " + message.getMessageId());
//    }
//
//    public static void main(String[] args) {
//        // 서비스 초기화
////        UserService userService = new FileUserService();
////        ChannelService channelService = new FileChannelService();
////        MessageService messageService = new FileMessageService(channelService, userService);
//
//        // 메모리 저장체
//          UserRepository userRepository = new JCFUserRepository();
//          ChannelRepository channelRepository = new JCFChannelRepository();
////          MessageRepository messageRepository = new JCFMessageRepository();
//
//        //직렬화 저장체
////        UserRepository userRepository = new FileUserRepository();
////        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();
//
//        //서비스 초기화
////          UserService userService = new BasicUserService(userRepository);
////          ChannelService channelService = new BasicChannelService(channelRepository);
////          MessageService messageService
////                  = new BasicMessageService(messageRepository, userRepository, channelRepository,null);
//
//        // CRUD테스트
////        userCRUDTest(userService);
////        channelCRUDTest(channelService);
////        messageCRUDTest(messageService);
//
////        userPrint(userService);
////        channelPrint(channelService);
////        messagePrint(messageService);
//
//
////        // 셋업
////        User user = setupUser(userService);
////        Channel channel = setupChannel(channelService);
//////        // 메시지 생성 테스트
////        setupUser(userService);
////        setupChannel(channelService);
////        messageCreateTest(messageService, channel, user);
//
//
//
//    }
//}
