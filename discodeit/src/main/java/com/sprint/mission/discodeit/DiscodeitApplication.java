package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.DTO.ChannelDTO;
import com.sprint.mission.discodeit.DTO.MessageDTO;
import com.sprint.mission.discodeit.DTO.UserDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

@SpringBootApplication
public class DiscodeitApplication {
	static User setupUser(UserService userService) {
		String unique = String.valueOf(System.currentTimeMillis());
		UserDTO.CreateUserDTO createUserDTO = new UserDTO.CreateUserDTO(
				"user: " + unique, "email: " + unique, "789","101112");
		return userService.createUser(createUserDTO);
	}

	static Channel setupChannel(ChannelService channelService){
		ChannelDTO.CreatePublicChannelDTO createPublicChannelDTO
				= new ChannelDTO.CreatePublicChannelDTO("공지", ChannelType.PUBLIC,"공지채널입니다",null );
		return channelService.createPublicChannel(createPublicChannelDTO);
	}

	static void messageCRUDTest(MessageService messageService, Channel channel, User user) {
		MessageDTO.CreateMessageDTO createMessageDTO
				= new MessageDTO.CreateMessageDTO("안녕하세요",channel.getChannelId(), user.getUserId(),null);
		Message message = messageService.createMessage(createMessageDTO);
		System.out.println("메시지 생성: " + message.getMessageId());
    }

	public static void main(String[] args) {
//		SpringApplication.run(DiscodeitApplication.class, args);

		ConfigurableApplicationContext context = SpringApplication.run(
				DiscodeitApplication.class, args);

		// 메모리 저장체
		UserRepository userRepository = new JCFUserRepository();
		ChannelRepository channelRepository = new JCFChannelRepository();
//          MessageRepository messageRepository = new JCFMessageRepository();

		//직렬화 저장체
//        UserRepository userRepository = new FileUserRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
		MessageRepository messageRepository = new FileMessageRepository();

		//서비스 초기화
		UserService userService = context.getBean(BasicUserService.class);
		ChannelService channelService = context.getBean(BasicChannelService.class);
		MessageService messageService= context.getBean(BasicMessageService.class);

		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);

		messageCRUDTest(messageService, channel, user);





	}

}
