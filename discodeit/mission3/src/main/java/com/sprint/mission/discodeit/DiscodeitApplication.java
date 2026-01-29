package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.DTO.MyUserDto;
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

import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {
	static User setupUser(UserService userService) {
		MyUserDto.BasicInfo userInfo=new MyUserDto.BasicInfo(
				"전승현",
				"asdasda@asdad.com",
				"1234"
		);
		User user = userService.create(userInfo);
		System.out.println("유저 생성!: " + user.getUsername());
		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
		System.out.println("채널 생성!: " + channel.getName()+" 채널 설명:" + channel.getDescription());
		return channel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
		System.out.println("메시지 생성: " + message.getId());
	}
	public static void main(String[] args) {
		ConfigurableApplicationContext context= SpringApplication.run(DiscodeitApplication.class,args);
		UserService userService=context.getBean(UserService.class);
		ChannelService channelService=context.getBean(ChannelService.class);
		MessageService messageService=context.getBean(MessageService.class);
		//생성
		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		messageCreateTest(messageService,channel,user);
		//find
		MyUserDto.FindInfo dto = userService.find(user.getId());
		System.out.println(dto);
		List<MyUserDto.FindInfo> dto1=userService.findAll();
		System.out.println(dto1);




		}
	}
