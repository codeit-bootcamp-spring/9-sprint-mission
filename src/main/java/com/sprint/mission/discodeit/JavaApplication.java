//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.ChannelType;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
//import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
//import com.sprint.mission.discodeit.service.basic.BasicUserService;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//
//import java.util.List;
//import java.util.UUID;
//
//
//public class JavaApplication {
//    static void userCRUDTest(UserService userService) {
//        // 생성
//        User user = userService.create("woody", "woody@codeit.com", "woody1234");
//        System.out.println("유저 생성: " + user.getId());
//        // 조회
//        User foundUser = userService.find(user.getId());
//        System.out.println("유저 조회(단건): " + foundUser.getId());
//        List<User> foundUsers = userService.findAll();
//        System.out.println("유저 조회(다건): " + foundUsers.size());
//        // 수정
//        User updatedUser = userService.update(user.getId(), null, null, "woody5678");
//        System.out.println("유저 수정: " + String.join("/", updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getPassword()));
//        // 삭제
//        userService.delete(user.getId());
//        List<User> foundUsersAfterDelete = userService.findAll();
//        System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
//    }
//
//    static void channelCRUDTest(ChannelService channelService) {
//        // 생성
//        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
//        System.out.println("채널 생성: " + channel.getId());
//        // 조회
//        Channel foundChannel = channelService.find(channel.getId());
//        System.out.println("채널 조회(단건): " + foundChannel.getId());
//        List<Channel> foundChannels = channelService.findAll();
//        System.out.println("채널 조회(다건): " + foundChannels.size());
//        // 수정
//        Channel updatedChannel = channelService.update(channel.getId(), "공지사항", null);
//        System.out.println("채널 수정: " + String.join("/", updatedChannel.getName(), updatedChannel.getDescription()));
//        // 삭제
//        channelService.delete(channel.getId());
//        List<Channel> foundChannelsAfterDelete = channelService.findAll();
//        System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());
//    }
//
//    static void messageCRUDTest(MessageService messageService) {
//        // 생성
//        UUID channelId = UUID.randomUUID();
//        UUID authorId = UUID.randomUUID();
//        Message message = messageService.create("안녕하세요.", channelId, authorId);
//        System.out.println("메시지 생성: " + message.getId());
//        // 조회
//        Message foundMessage = messageService.find(message.getId());
//        System.out.println("메시지 조회(단건): " + foundMessage.getId());
//        List<Message> foundMessages = messageService.findAll();
//        System.out.println("메시지 조회(다건): " + foundMessages.size());
//        // 수정
//        Message updatedMessage = messageService.update(message.getId(), "반갑습니다.");
//        System.out.println("메시지 수정: " + updatedMessage.getContent());
//        // 삭제
//        messageService.delete(message.getId());
//        List<Message> foundMessagesAfterDelete = messageService.findAll();
//        System.out.println("메시지 삭제: " + foundMessagesAfterDelete.size());
//    }
//
//    static User setupUser(UserService userService) {
//        User user = userService.create("woody", "woody@codeit.com", "woody1234");
//        return user;
//    }
//
//    static Channel setupChannel(ChannelService channelService) {
//        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
//        return channel;
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
//        System.out.println("메시지 생성: " + message.getId());
//    }
//
//    public static void main(String[] args) {
//
//        ApplicationContext context =
//                new AnnotationConfigApplicationContext(DiscodeitApplication.class);
////        // 레포지토리 초기화
////
////        UserRepository userRepository = new FileUserRepository();
////        ChannelRepository channelRepository = new FileChannelRepository();
////        MessageRepository messageRepository = new FileMessageRepository();
////
////        // 서비스 초기화
//        UserService userService = context.getBean(UserService.class);
//        ChannelService channelService = context.getBean(ChannelService.class);
//        MessageService messageService = context.getBean(MessageService.class);
////
//        // 셋업
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//        // 테스트
//        messageCreateTest(messageService, channel, user);
//    }
//}