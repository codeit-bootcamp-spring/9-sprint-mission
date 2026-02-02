package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.DTO.ChannelService.Request.CreatePrivateChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.Request.CreatePublicChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.Response.FindChannelResponse;
import com.sprint.mission.discodeit.DTO.ChannelService.Request.UpdateChannelRequest;
import com.sprint.mission.discodeit.DTO.MessageService.Request.CreateMessageRequest;
import com.sprint.mission.discodeit.DTO.UserService.Request.CreateUserRequest;
import com.sprint.mission.discodeit.DTO.UserService.Response.FindUserResponse;
import com.sprint.mission.discodeit.DTO.UserService.Request.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	static void userCRUDTest(UserService userService) {
		System.out.println("***** 유저 CRID Test *****");
		// 생성
		User user = userService.create(new CreateUserRequest("한성재", "abc@asd.com", "asdf1234", null));
		System.out.println("유저 생성: " + user.toString());
		User user2 = userService.create(new CreateUserRequest("신짱구", "fdsa@asd.com", "3213fdfd", null));
		System.out.println("유저 생성: " + user2.toString());
		// 조회
		FindUserResponse foundUser = userService.findByID(user.getId());
		System.out.println("유저 조회(단건): " + foundUser.toString());
		List<FindUserResponse> foundUsers = userService.findAll();
		System.out.println("유저 조회(다건): " + foundUsers.size());
		// 수정
		User updatedUser = userService.update(new UpdateUserRequest(user.getId(), "김철수", "mnb123", "qwer@asdf.com", null));
		System.out.println("유저 수정: " + String.join("/", updatedUser.toString()));
		// 삭제
		userService.remove(user.getId());
		List<FindUserResponse> foundUsersAfterDelete = userService.findAll();
		System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
	}

	static void channelCRUDTest(UserService userService, ChannelService channelService) {
		System.out.println("\n***** 채널 CRID Test *****");
		// 생성
		Channel channel = channelService.createPrivateChannel(new CreatePrivateChRequest(userService.findAll().stream()
				.map(FindUserResponse::id)
				.toList()));
		System.out.println("PRIVATE 채널 생성: " + channel.getId());

		Channel channel2 = channelService.createPublicChannel(new CreatePublicChRequest("채널 테스트용 채널", "테스트용 이에용"));
		System.out.println("PUBLIC 채널 생성: " + channel2.getId());

		// 조회
		FindChannelResponse foundChannel = channelService.findByID(channel.getId());
		System.out.println("채널 조회(단건): " + foundChannel.toString());
		List<FindChannelResponse> foundChannels = channelService.findAll();
		System.out.println("채널 조회(다건): " + foundChannels.size());
		// 수정
		Channel updatedChannel = channelService.update(new UpdateChannelRequest(channel2.getId(), "채널 수정 테스트", "테스트 끝남"));
		System.out.println("채널 수정: " + String.join("/", updatedChannel.toString()));
		// 삭제
		channelService.remove(channel.getId());
		List<FindChannelResponse> foundChannelsAfterDelete = channelService.findAll();
		System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());
	}

	static void messageCRUDTest(MessageService messageService, ChannelService channelService) {
		System.out.println("\n***** 메시지 CRID Test *****");
		// 테스트용 채널
		Channel channel = channelService.createPublicChannel(new CreatePublicChRequest("테스트용 채널", "메시지 테스트"));

		// 생성
		UUID channelId = channel.getId();
		UUID authorId = UUID.randomUUID();
		Message message = messageService.create(new CreateMessageRequest(authorId, channelId, "안녕하세요?",new ArrayList<>()));
		System.out.println("메시지 생성: " + message.getId());
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


		// 테스트
		System.out.println("\n***** Test *****\n");
		userCRUDTest(userService);
		channelCRUDTest(userService, channelService);
		messageCRUDTest(messageService, channelService);
	}

}
