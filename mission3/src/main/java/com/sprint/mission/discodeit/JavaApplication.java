//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.DTO.MyUserDto;
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
//
//public class JavaApplication {
//    static User setupUser(UserService userService) {
//        MyUserDto.BasicInfo userInfo=new MyUserDto.BasicInfo(
//                "전승현",
//                "asdasda@asdad.com",
//                "1234"
//        );
//        MyUserDto.AllInfo info = new MyUserDto.AllInfo(userInfo,null);
//        User user = userService.create(info);
//        System.out.println("유저 생성!: " + user.getUsername());
//        return user;
//    }
//    static Channel setupChannel(ChannelService channelService) {
//        Channel channel = channelService.createPublicChannel(ChannelType.PUBLIC, null);
//        Channel channel1= channelService.createPrivateChannel(ChannelType.PRIVATE,null);
//        return channel;
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
//        System.out.println("메시지 생성: " + message.getId());
//    }
//
//    public static void main(String[] args) {
//        // 레포지토리 초기화
//        UserRepository userRepository = new FileUserRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();
//
//        // 서비스 초기화
//        UserService userService = new BasicUserService(userRepository,null,null);
//        ChannelService channelService = new BasicChannelService(channelRepository,null,null);
//        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository,null);
//
//        // 셋업
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//        // 테스트
//        messageCreateTest(messageService, channel, user);
//    }
//}
