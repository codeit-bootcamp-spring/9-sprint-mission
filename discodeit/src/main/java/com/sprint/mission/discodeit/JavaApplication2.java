//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
//import com.sprint.mission.discodeit.dto.MessageCreateRequest;
//import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
//import com.sprint.mission.discodeit.dto.UserCreateRequest;
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.ChannelType;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.*;
//import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
//import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
//import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserRepository;
//import com.sprint.mission.discodeit.repository.jcf.JcfReadStatusRepository;
//import com.sprint.mission.discodeit.repository.jcf.JcfUserStatusRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.ReadStatusService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
//import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
//import com.sprint.mission.discodeit.service.basic.BasicUserService;
//
//import java.util.Optional;
//
//public class JavaApplication2 {
//    static User setupUser(UserService userService) {
//        UserCreateRequest request = new UserCreateRequest(
//                "woody",
//                "woody@codeit.com",
//                "woody1234",
//                null
//        );
//        return userService.create(request, Optional.empty());
//    }
//    private static Channel setupChannel(ChannelService channelService) {
//        ChannelCreateRequest request = new ChannelCreateRequest(
//                "공지",
//                "공지 채널입니다.",
//                null
//        );
//        return channelService.createPublic(request);
//    }
//
//    private static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        MessageCreateRequest request = new MessageCreateRequest(
//                "안녕하세요.",
//                channel.getId(),
//                author.getId(),
//                null                // 첨부파일(지금은 없으므로 null 전달)
//        );
//
//        Message message = messageService.create(request);
//        System.out.println("메시지 생성: " + message.getId());
//    }
//    public static void main(String[] args) {
//        // 레포지토리 초기화
//        UserRepository userRepository = new FileUserRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();
//        ReadStatusRepository readStatusRepository = new JcfReadStatusRepository();
//        BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository();
//
//        // 서비스 초기화
//        UserStatusRepository userStatusRepository = new JcfUserStatusRepository();
//        UserService userService = new BasicUserService(userRepository, userStatusRepository);
//        ChannelService channelService = new BasicChannelService(channelRepository, readStatusRepository, messageRepository);
//        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository, binaryContentRepository);
//        ReadStatusService readStatusService = new BasicReadStatusService(readStatusRepository, userRepository, channelRepository);
//
//        // 셋업
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//        // 테스트
//        messageCreateTest(messageService, channel, user);
//        ReadStatusCreateRequest rsRequest = new ReadStatusCreateRequest(
//                user.getId(),
//                channel.getId(),
//                null // 처음에는 읽은 메시지가 없으므로 null을 넣습니다.
//        );
//
//        readStatusService.create(rsRequest);
//        System.out.println("읽음 상태 생성 완료!");
//    }
//}
