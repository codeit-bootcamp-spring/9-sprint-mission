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
import org.springframework.context.ConfigurableApplicationContext;

//import static com.sprint.mission.discodeit.JavaApplication.*;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

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
		// 신청서 제출
		return userService.create(request);
	}

	static ChannelResponse setupChannel(ChannelService channelService) {
		PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "공지 채널입니다.");
		return channelService.createPublic(request);
	}

	static void messageCreateTest(MessageService messageService, ChannelResponse channel, UserResponse author) {
		MessageCreateRequest request = new MessageCreateRequest("안녕하세요.", channel.id(), author.id(), null);

		MessageResponse message = messageService.create(request);
		System.out.println("메시지 생성: " + message.id());
	}

}
