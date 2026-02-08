package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context =
				SpringApplication.run(DiscodeitApplication.class, args);

		UserService basicUserService = context.getBean(UserService.class);
		ChannelService basicChannelService = context.getBean(ChannelService.class);
		MessageService basicMessageService = context.getBean(MessageService.class);

		UserRepository userRepository = context.getBean(UserRepository.class);
		ChannelRepository channelRepository = context.getBean(ChannelRepository.class);
		MessageRepository messageRepository = context.getBean(MessageRepository.class);

		User user = setupUser(userRepository);
		UUID ownerId = user.getId();
		Channel channel = setupChannel(channelRepository, ownerId);

		messageCRUDTest(messageRepository, channel, user);
	}

	static void channelCRUDTest(ChannelRepository channelRepository, UUID ownerId) {
		UUID id = UUID.randomUUID();
		Channel channel = new Channel(null, "공지사항", "공지 채널입니다.");
		channelRepository.save(channel);
		id = channel.getId();
		System.out.println("채널 생성: " + id);

		Channel found = channelRepository.findById(id).orElse(null);
		System.out.println("채널 조회(findById): " + (found == null ? "null" : found.getId()));

		System.out.println("채널 전체조회: " + channelRepository.findAll().size());

		Channel updated = new Channel(null,"공지사항","공지사항입니다.");
		channelRepository.save(updated);

		Channel afterUpdate = channelRepository.findById(id).orElse(null);
		System.out.println("채널 수정 후: " + (afterUpdate == null ? "null" : afterUpdate.getId()));

		System.out.println("채널 존재 여부: " + channelRepository.existsById(id));

		channelRepository.deleteById(id);
		System.out.println("채널 삭제 후 존재 여부: " + channelRepository.existsById(id));
	}

	static void userCRUDTest(UserRepository userRepository) {
		User user = new User("testUser", "test@codeit.com", "test1234");
		userRepository.save(user);
		UUID id = user.getId();
		System.out.println("유저 생성: " + id);

		User found = userRepository.findById(id).orElse(null);
		System.out.println("유저 조회(findById): " + (found == null ? "null" : found.getUsername()));

		List<User> all = userRepository.findAll();
		System.out.println("유저 전체조회(findAll) 개수: " + all.size());

		User updated = new User("testUser", "updated@codeit.com", "test1234");
		userRepository.save(updated);

		User afterUpdate = userRepository.findById(id).orElse(null);
		System.out.println("유저 수정 후: " + (afterUpdate == null ? "null" : afterUpdate.getUsername()));

		System.out.println("유저 존재 여부(existsById): " + userRepository.existsById(id));

		userRepository.deleteById(id);
		System.out.println("유저 삭제 후 존재 여부: " + userRepository.existsById(id));
	}

	static void messageCRUDTest(MessageRepository messageRepository, Channel channel, User author) {
		UUID id;
		UUID authorId =  author.getId();
		UUID channelId = channel.getId();
		Message message = new Message("메시지메시지",channelId, authorId);

		messageRepository.save(message);
		id = message.getId();
		System.out.println("메시지 생성: " + id);

		Message found = messageRepository.findById(id).orElse(null);
		System.out.println("메시지 조회(findById): " + (found == null ? "null" : found.getContent()));

		System.out.println("메시지 전체조회: " + messageRepository.findAll().size());

		Message updated = new Message("안녕안녕",channelId, authorId);
		messageRepository.save(updated);

		Message afterUpdate = messageRepository.findById(id).orElse(null);
		System.out.println("메시지 수정 후: " + (afterUpdate == null ? "null" : afterUpdate.getContent()));

		System.out.println("메시지 존재 여부: " + messageRepository.existsById(id));

		messageRepository.deleteById(id);
		System.out.println("메시지 삭제 후 존재 여부: " + messageRepository.existsById(id));
	}

	static User setupUser(UserRepository userRepository) {
		User user = new User("woody", "woody@codeit.com","woody1234");
		userRepository.save(user);
		return user;
	}

	static Channel setupChannel(ChannelRepository channelRepository, UUID ownerId) {
		UUID id = UUID.randomUUID();
		Channel channel = new Channel(null,"woody","woody");
		channelRepository.save(channel);
		return channel;
	}
}
