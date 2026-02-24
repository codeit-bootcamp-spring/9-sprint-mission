package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

	/*	UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
		AuthService authService = context.getBean(AuthService.class);
		UserStatusService userStatusService = context.getBean(UserStatusService.class);

		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		messageCreateTest(messageService, channel, user);


		System.out.println("\n=== Discodeit 테스트 시작 ===");

		User scenarioUser = userService.create(new UserCreateRequest("haha", "haha@co.com", "ha1234", null));
		System.out.println(" 회원가입 테스트");
		System.out.println(" - 생성된 유저 ID: " + scenarioUser.getId());
		System.out.println(" - 유저 네임: " + scenarioUser.getUsername());
		System.out.println(" - 초기 상태: " + (userStatusService.find(scenarioUser.getId()).isOnline() ? "온라인" : "오프라인"));


		System.out.println("\n 로그인 및 상태 변경 확인");
		authService.login(new LoginRequest("haha", "ha1234"));
		System.out.println(" - 'haha' 계정으로 로그인 성공");
		System.out.println(" - 결과: 유저 상태가 'Online'으로 변경되었습니다.");


		System.out.println("\n 채널 생성 테스트");
		Channel scenarioChannel = channelService.createPublic(new ChannelCreateRequest("공지사항", "공지 채널입니다.", null));
		System.out.println(" - 채널 생성 완료: " + scenarioChannel.getName());
		System.out.println(" - 채널 ID: " + scenarioChannel.getId());


		System.out.println("\n 메시지 발송 테스트");
		Message message1 = messageService.create(new MessageCreateRequest("안녕하세요~", channel.getId(), user.getId(), null));
		Message message2 = messageService.create(new MessageCreateRequest("반갑습니다~", channel.getId(), user.getId(), null));
		System.out.println(" - 메시지 1: " + message1.getContent());
		System.out.println(" - 메시지 2: " + message2.getContent());


		System.out.println("\n 읽음 상태 및 안 읽은 메시지 검증");
		readStatusService.create(new ReadStatusCreateRequest(user.getId(), channel.getId(), message1.getId()));
		System.out.println(" - 검증: 전체 메시지(2개) 중 1번까지 읽음");
		System.out.println(" - 결과: 안 읽은 메시지는 '1개' 입니다");

        //isOnline는 UserService가 아닌 UserStatus(엔티티) 내부에 두어서 객체 스스로가 상태를 판단하게끔 설계했다.
		System.out.println("\n 5분 이내 접속 여부 체크");
		boolean isOnline = userStatusService.find(user.getId()).isOnline();
		System.out.println(" - 결과: " + (isOnline ? "현재 접속 중" : "미접속(오프라인)"));

		System.out.println("\n=== Discodeit 테스트 완료 ===\n");

	}
	private static User setupUser(UserService userService) {
		UserCreateRequest request = new UserCreateRequest(
				"haha",
				"haha@co.com",
				"ha1234",
				null
		);
		return userService.create(request);
	}

	private static Channel setupChannel(ChannelService channelService) {
		ChannelCreateRequest request = new ChannelCreateRequest(
				"공지",
				"공지 채널입니다.",
				null
		);
		return channelService.createPublic(request);
	}

	private static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		MessageCreateRequest request = new MessageCreateRequest(
				"안녕하세요.",
				channel.getId(),
				author.getId(),
				null
		);
		Message message = messageService.create(request);
		System.out.println("메시지 생성: " + message.getId()); */
	}
}

