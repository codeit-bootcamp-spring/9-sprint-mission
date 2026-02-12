//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequestDto;
//import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
//import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
//import com.sprint.mission.discodeit.dto.user.UserResponseDto;
//import com.sprint.mission.discodeit.entity.ChannelType;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.repository.*;
//import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
//import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
//import com.sprint.mission.discodeit.service.basic.BasicUserService;
//
//public class JavaApplication {
//    static UserResponseDto setupUser(UserService userService) {
////        DTO 활용하여 객체 가져오기
//        UserCreateRequestDto requestDto = new UserCreateRequestDto(
//                "woody",
//                "woody@codeit.com",
//                "woody1234",
//                null
//        );
//
//        UserResponseDto user = userService.create(requestDto);
//        return userService.create(requestDto);
//    }
//
//    static ChannelResponseDto setupChannel(ChannelService channelService) {
////        ChannelResponseDto channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
//        return channelService.createPublic(
//                new ChannelCreateRequestDto(
//                        ChannelType.PUBLIC,
//                        "코드잇",
//                        "코드잇 4팀 채널입니다."
//                )
//        );
//    }
//
//    static void messageCreateTest(MessageService messageService, ChannelResponseDto channel, UserResponseDto author) {
//        MessageCreateRequestDto dto =
//                new MessageCreateRequestDto(
//                        "안녕하세요.",
//                        channel.id(),
//                        author.id(),
//                        null
//                );
//        Message message = messageService.create(dto);
//        System.out.println("메시지 생성: " + message.getId());
//    }
//
//    public static void main(String[] args) {
//        // 레포지토리 초기화
//        UserRepository userRepository = new FileUserRepository();
//        UserStatusRepository userStatusRepository = new FileUserStatusRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();
//        BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository() {
//        };
//
//        // 서비스 초기화
//        UserService userService = new BasicUserService(userRepository, userStatusRepository);
//        ChannelService channelService = new BasicChannelService(channelRepository);
//        BinaryContentService binaryContentService = new BasicBinaryContentService(binaryContentRepository);
//        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository, binaryContentService);
//
//        // 셋업
//        UserResponseDto user = setupUser(userService);
//        ChannelResponseDto channel = setupChannel(channelService);
//        // 테스트
//        messageCreateTest(messageService, channel, user);
//    }
//}
