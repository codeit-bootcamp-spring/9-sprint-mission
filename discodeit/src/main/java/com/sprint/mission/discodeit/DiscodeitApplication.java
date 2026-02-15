package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

//		UserService userService = context.getBean(UserService.class);
//		ChannelService channelService = context.getBean(ChannelService.class);
//		MessageService messageService = context.getBean(MessageService.class);
//		AuthService authService = context.getBean(AuthService.class);
//		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
//		UserStatusService userStatusService = context.getBean(UserStatusService.class);
//		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

		//List<UserResponse> responseList = userService.findAll();
	}

}
