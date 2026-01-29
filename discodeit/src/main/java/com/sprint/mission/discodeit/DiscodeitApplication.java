package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.DTO.UserCreatRequest;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static com.sprint.mission.discodeit.DiscodeitApplication.JavaApplication.*;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		// 셋업
		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		// 테스트
		messageCreateTest(messageService, channel, user);

	}

	public class JavaApplication {
		static User setupUser(UserService userService) {
			UserCreatRequest request = new UserCreatRequest("woody", "woody@codeit.com", "woody1234",null);
			return userService.create(request);
		}

		static Channel setupChannel(ChannelService channelService) {
			Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
			return channel;
		}

		static void messageCreateTest(MessageService messageService, Channel channel, User author) {
			Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
			System.out.println("메시지 생성: " + message.getId());
		}


	}
}
