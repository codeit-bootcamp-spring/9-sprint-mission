package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.DTO.AuthService.LoginRequest;
import com.sprint.mission.discodeit.DTO.BinaryContentService.Request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.Request.CreatePrivateChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.Request.CreatePublicChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.Response.FindChannelResponse;
import com.sprint.mission.discodeit.DTO.ChannelService.Request.UpdateChannelRequest;
import com.sprint.mission.discodeit.DTO.MessageService.Request.CreateMessageRequest;
import com.sprint.mission.discodeit.DTO.UserService.Request.CreateUserRequest;
import com.sprint.mission.discodeit.DTO.UserService.Response.FindUserResponse;
import com.sprint.mission.discodeit.DTO.UserService.Request.UpdateUserRequest;
import com.sprint.mission.discodeit.DTO.UserStatusService.Request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.Basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.util.*;

import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	// 테스트용 문자열 생성
	static String randomString(int length) {
		Random rString = new Random();
		return rString.ints(length, 0, 62)
				.mapToObj("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"::charAt)
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString();
	}
	// 테스트용 바이너리 데이터 생성
	static byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];
		new java.security.SecureRandom().nextBytes(bytes);
		return bytes;
	}

	static void userCRUDTest(UserService userService, AuthService authService, UserStatusService userStautusService) {
		System.out.println("***** 유저 CRID Test *****");
		// 생성
		try {
			String userName = randomString(4);
			String userEmail = randomString(6) + "@naver.com";
			String passWord = randomString(8);

			User user1 = userService.create(new CreateUserRequest(userName, userEmail, passWord, randomBytes(1024)));
			//System.out.println("유저 생성: " + user1.toString());
			User user2 = userService.create(new CreateUserRequest(randomString(4), randomString(6) + "@naver.com"
					, randomString(8), randomBytes(1024)));
			//System.out.println("유저 생성: " + user2.toString());

			// 로그인 테스트
			System.out.println("유저 로그인: " + authService.Login(new LoginRequest(userName, passWord)));

			// 조회
			FindUserResponse foundUser = userService.findByID(user1.getId());
			System.out.println("유저 조회(단건): " + foundUser.toString());
			List<FindUserResponse> foundUsers = userService.findAll();
			System.out.println("유저 조회(다건): " + foundUsers.size());
			// 수정
			User updatedUser = userService.update(new UpdateUserRequest(user1.getId(), randomString(3), randomString(5) + "@gmail.com"
					, randomString(7), null));
			System.out.println("첫번째 유저 수정: " + String.join("/", updatedUser.toString()));

			System.out.println("두번째 유저 접속 상태: " + userStautusService.find(user2.getUserStateId()).checkIsLogin());

			// 삭제
			userService.remove(user2.getId());
			List<FindUserResponse> foundUsersAfterDelete = userService.findAll();
			System.out.println("두번째 유저 삭제: " + foundUsersAfterDelete.size());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static void channelCRUDTest(UserService userService, ChannelService channelService, ReadStatusService readStatusService) {
		System.out.println("\n***** 채널 CRID Test *****");
		// 생성
		List<UUID> users = userService.findAll().stream()
				.map(FindUserResponse::id)
				.toList();
		System.out.println("PRIVATE 채널 생성");
		Channel channel = channelService.createPrivateChannel(new CreatePrivateChRequest(users));

		System.out.println("PUBLIC 채널 생성");
		Channel channel2 = channelService.createPublicChannel(new CreatePublicChRequest("채널 테스트용 채널", "테스트용 이에용"));

		// 조회
		FindChannelResponse foundChannel = channelService.findByID(channel.getId());
		System.out.println("채널 조회(단건): " + foundChannel.toString());
		List<FindChannelResponse> foundChannels = channelService.findAll();
		System.out.println("채널 조회(다건): " + foundChannels.size());
		// 수정
		Channel updatedChannel = channelService.update(new UpdateChannelRequest(channel2.getId(), "채널 수정 테스트", "테스트 끝남"));
		System.out.println("채널 수정: " + String.join("/", updatedChannel.toString()));

		ReadStatus readStatus = readStatusService.findAllbyUserId(users.get(0)).stream()
                .filter(rs -> rs.getChannelId().equals(channel.getId()))
                .findFirst().orElseThrow();
		readStatus.updateLastReadAt(Instant.now());
		System.out.println("메시지 읽은 시간 수정: " + readStatus.toString());
		// 삭제
		channelService.remove(channel.getId());
		List<FindChannelResponse> foundChannelsAfterDelete = channelService.findAll();
		System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());
	}

	static void messageCRUDTest(MessageService messageService, ChannelService channelService, BinaryContentService binaryContentService) {
		System.out.println("\n***** 메시지 CRID Test *****");
		// 테스트용 채널
		Channel channel = channelService.createPublicChannel(new CreatePublicChRequest("테스트용 채널", "메시지 테스트"));

		// 생성
		UUID channelId = channel.getId();
		UUID authorId = UUID.randomUUID();

		List<UUID> attachmentList = new ArrayList<>();
		BinaryContent attachment = binaryContentService.create(new CreateBinaryContentRequest(
				BinaryContentOwnerType.Message,
				authorId,
				randomBytes(1024)
		));
		attachmentList.add(attachment.getId());
		Message message = messageService.create(new CreateMessageRequest(authorId, channelId, "안녕하세요?",attachmentList));
		//System.out.println("메시지 생성: " + message.getId());
		// 조회
		Message foundMessage = messageService.findByID(message.getId());
		System.out.println("메시지 조회(단건): " + foundMessage.toString());
		List<Message> foundMessages = messageService.findAllByChannelId(channelId);
		System.out.println("메시지 조회(다건): " + foundMessages.size());
		// 수정
		Message updatedMessage = messageService.updateContent(message.getId(), "반갑습니다.");
		System.out.println("메시지 수정: " + String.join("/", updatedMessage.toString()));
		// 삭제
		messageService.remove(message.getId());
		List<Message> foundMessagesAfterDelete = messageService.findAllByChannelId(channelId);
		System.out.println("메시지 삭제: " + foundMessagesAfterDelete.size());
	}

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		AuthService authService = context.getBean(AuthService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
		UserStatusService userStautusService = context.getBean(UserStatusService.class);
		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

		// 테스트
		System.out.println("\n***** Test *****\n");
		userCRUDTest(userService, authService, userStautusService);
		channelCRUDTest(userService, channelService, readStatusService);
		messageCRUDTest(messageService, channelService, binaryContentService);
	}

}
