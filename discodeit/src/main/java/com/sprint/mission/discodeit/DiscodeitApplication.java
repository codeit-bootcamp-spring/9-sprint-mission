package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

//import static com.sprint.mission.discodeit.JavaApplication.*;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		UserResponse user = setupUser(userService);
		ChannelResponse channel = setupChannel(channelService);
		messageCreateTest(messageService, channel, user);
	}

	static UserResponse setupUser(UserService userService) {
		UserCreateRequest request = new UserCreateRequest(
				"woody", "woody@codeit.com", "woody1234", null, null
		);

		try {
			UserResponse newUser = userService.create(request);
			System.out.println("\n========== 신규 유저가 생성되었습니다 ==========");
			System.out.println("신규유저 ID: " + newUser.id());
			System.out.println("신규유저 이름: " + newUser.username());
			System.out.println("신규유저 비밀번호: " + newUser.password());
			System.out.println("신규유저 이메일: " + newUser.email());
			return newUser;

		} catch (IllegalArgumentException e) {
			UserResponse existingUser = userService.findAll().stream()
					.filter(u -> u.email().equals(request.email()))
					.findFirst()
					.orElseThrow();

			System.out.println("\n========== 기존 유저 데이터를 불러옵니다 ==========");
			System.out.println("기존유저 ID: " + existingUser.id());
			System.out.println("기존유저 이름: " + existingUser.username());
			System.out.println("기존유저 비밀번호: " + existingUser.password());
			System.out.println("기존유저 이메일: " + existingUser.email());
			System.out.println("온라인 여부: " + existingUser.online());

			return existingUser;
		}
	}

	static ChannelResponse setupChannel(ChannelService channelService) {
		PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "공지 채널입니다.");

		try {
			ChannelResponse newChannel = channelService.createPublic(request);
			System.out.println("\n========== 신규 채널이 생성되었습니다✨ ==========");
			System.out.println("신규채널 ID: " + newChannel.id());
			System.out.println("신규채널 이름: " + newChannel.name());
			System.out.println("신규채널 설명: " + newChannel.description());
			return newChannel;

		} catch (IllegalArgumentException f) {
			ChannelResponse existingChannel = channelService.findAll().stream()
					.filter(c -> c.name().equals(request.name()))
					.findFirst()
					.orElseThrow();

			System.out.println("\n========== 기존 채널 데이터를 불러옵니다 ==========");
			System.out.println("기존채널 ID      : " + existingChannel.id());
			System.out.println("기존채널 이름    : " + existingChannel.name());
			System.out.println("기존채널 설명  : " + existingChannel.description());

			return existingChannel;
		}
	}

	static void messageCreateTest(MessageService messageService, ChannelResponse channel, UserResponse author) {
		MessageCreateRequest request = new MessageCreateRequest("안녕하세요.", channel.id(), author.id(), null);

		MessageResponse message = messageService.create(request);

		System.out.println("\n============ 메시지가 생성되었습니다 ============");
		System.out.println("메시지 ID: " + message.id());
		System.out.println("메시지 생성: " + message.content());
		System.out.println("채널 ID: " + message.channelId());
		System.out.println("유저 ID: " + message.authorId());
		System.out.println("첨부파일: " + message.attachmentIds());

	}

}
