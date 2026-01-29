package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

//	static void userCRUDTest(UserService userService) {
//		System.out.println("***** 유저 CRID Test *****");
//		// 생성
//		User user = userService.create("woody", "01019427577", "woody1234@codeit.com");
//		System.out.println("유저 생성: " + user.getId());
//		// 조회
//		User foundUser = userService.findByID(user.getId());
//		System.out.println("유저 조회(단건): " + foundUser.toString());
//		List<User> foundUsers = userService.getAll();
//		System.out.println("유저 조회(다건): " + foundUsers.size());
//		// 수정
//		User updatedUser = userService.update(user.getId(), "hellooo", "01099999999", "hellooo8@codeit.com");
//		System.out.println("유저 수정: " + String.join("/", updatedUser.toString()));
//		// 삭제
//		userService.remove(user.getId());
//		List<User> foundUsersAfterDelete = userService.getAll();
//		System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
//	}
//
//	static void channelCRUDTest(ChannelService channelService) {
//		System.out.println("\n***** 채널 CRID Test *****");
//		// 생성
//		Channel channel = channelService.create(ChannelType.PUBLIC, "공지");
//		System.out.println("채널 생성: " + channel.getId());
//		// 조회
//		Channel foundChannel = channelService.findByID(channel.getId());
//		System.out.println("채널 조회(단건): " + foundChannel.toString());
//		List<Channel> foundChannels = channelService.getAll();
//		System.out.println("채널 조회(다건): " + foundChannels.size());
//		// 수정
//		Channel updatedChannel = channelService.updateName(channel.getId(), "공지사항");
//		System.out.println("채널 수정: " + String.join("/", updatedChannel.toString()));
//		// 삭제
//		channelService.remove(channel.getId());
//		List<Channel> foundChannelsAfterDelete = channelService.getAll();
//		System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());
//	}
//
//	static void messageCRUDTest(MessageService messageService, ChannelService channelService) {
//		System.out.println("\n***** 메시지 CRID Test *****");
//		// 테스트용 채널
//		Channel channel = channelService.create(ChannelType.PRIVATE, "메시지 테스트용 채널");
//
//		// 생성
//		UUID channelId = channel.getId();
//		UUID authorId = UUID.randomUUID();
//		Message message = messageService.create(channelId, authorId, "안녕하세요.");
//		System.out.println("메시지 생성: " + message.getId());
//		// 조회
//		Message foundMessage = messageService.findByID(message.getId());
//		System.out.println("메시지 조회(단건): " + foundMessage.toString());
//		List<Message> foundMessages = messageService.getAll();
//		System.out.println("메시지 조회(다건): " + foundMessages.size());
//		// 수정
//		Message updatedMessage = messageService.updateContent(message.getId(), "반갑습니다.");
//		System.out.println("메시지 수정: " + String.join("/", updatedMessage.toString()));
//		// 삭제
//		messageService.remove(message.getId());
//		List<Message> foundMessagesAfterDelete = messageService.getAll();
//		System.out.println("메시지 삭제: " + foundMessagesAfterDelete.size());
//	}
//
//	static User setupUser(UserService userService) {
//		return userService.create("woody", "woody@codeit.com", "woody1234");
//	}
//
//	static Channel setupChannel(ChannelService channelService) {
//		return channelService.create(ChannelType.PUBLIC, "공지");
//	}
//
//	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//		Message message = messageService.create(channel.getId(), author.getId(), "안녕하세요.");
//		System.out.println("메시지 생성: " + message.getId());
//	}


	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);


//		// 테스트
//		System.out.println("\n***** Test *****\n");
//		userCRUDTest(userService);
//		channelCRUDTest(channelService);
//		messageCRUDTest(messageService, channelService);
	}

}
