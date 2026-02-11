package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static com.sprint.mission.discodeit.JavaApplication.*;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
//		context를 이용하여 Bean 생성 후 가져옴
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

			UserService userService = context.getBean(BasicUserService.class);

			ChannelService channelService = context.getBean(BasicChannelService.class);

			MessageService messageService = context.getBean(BasicMessageService.class);
//			셋업
			UserResponseDto user = setupUser(userService);
			System.out.println(user.id());
			ChannelResponseDto channel = setupChannel(channelService);
//			테스트
			messageCreateTest(messageService, channel, user);

	}
}