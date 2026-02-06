package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.DTO.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	static User TestUser(UserService userService, BinaryContentService binaryContentService,BasicAuthService basicAuthService,UserStatusService userStatusService) {
		BinaryContentDto.createDto createDto = new BinaryContentDto.createDto(
				"테스트파일.jpg",
				"image/jpeg"
		);
		BinaryContent profileFile =binaryContentService.create(createDto);
		System.out.println("프로필 파일 생성 완료:" + profileFile.getId());
		MyUserDto.profileInfo profile  = new MyUserDto.profileInfo(
				profileFile.getId(),
				profileFile.getFileName(),
				profileFile.getContentType()
		);

		MyUserDto.BasicInfo basicInfo = new MyUserDto.BasicInfo(
				"전승현","asd@naver.com","1234"
		);

		MyUserDto.AllInfo info = new MyUserDto.AllInfo(basicInfo,profile);;
		User user = userService.create(info);

		System.out.println("유저 생성!: " + user.getUsername()+ "(프로필 ID:" + user.getProfileId()+ ")");

		User loggedUser= loginUser(basicAuthService,userStatusService,user);
		if(loggedUser==null){
			throw new NoSuchElementException("로그인 실패");
		}

		MyUserDto.FindInfo dto = userService.find(user.getId());
		System.out.println("단건 조회:" + dto);
		List<MyUserDto.FindInfo> dto1=userService.findAll();
		System.out.println("전체 조회: " +  dto1);




		MyUserDto.UpdateInfo dto2= new MyUserDto.UpdateInfo(
				user.getId(),
				"전팝콘",
				"pop@kasd.com",
				"1234",
				"asdasd",
				"image.jpg2"
		);
		userService.update(dto2);
		MyUserDto.FindInfo findUser = userService.find(user.getId());
		if(findUser.username().equals("전팝콘")){
			System.out.println("업데이트 된 유저: " + findUser);
			System.out.println("성공적으로 이름이 변경됌.");
		}else{
			System.err.println("실패햇음");
		}
		return user;
	}
	static User loginUser(BasicAuthService basicAuthService,UserStatusService userStatusService,User user){
		System.out.println("=====>로그인 테스트<====");
		LoginDto.LoginUser loginUser = new LoginDto.LoginUser(
				user.getUsername(),
				"1234",
				user.getEmail()
		);
		try{
			User loginStatusUser = basicAuthService.login(loginUser);
			System.out.println(loginStatusUser.getUsername() + ":로그인 성공");
			UserStatusDto.updateUserStatus userStatus = new UserStatusDto.updateUserStatus(
					user.getId(),
					true
			);
			userStatusService.update(userStatus);
			System.out.println("유저 상태가 ONLINE입니다!");
			return loginStatusUser;
		}catch(Exception e){
			System.out.println("로그인 실패: " +e.getMessage());
			return null;
		}
		}





	static Channel testChannel(ChannelService channelService,User owner) {
		ChannelDto.PublicDto publicDto = new ChannelDto.PublicDto(owner.getId(),"공지사항","공지입니다");
		Channel channel = channelService.createPublicChannel(ChannelType.PUBLIC, publicDto);
		System.out.println("\n====>채널 테스트<====\n");
		List<UUID> userList = List.of(owner.getId());
		System.out.println("PUBLIC 채널 생성!: " + channel.getName()+" 채널 설명:" + channel.getDescription());
		ChannelDto.PrivateDto privateDto = new ChannelDto.PrivateDto(owner.getId(),userList);
		Channel channel1 =channelService.createPrivateChannel(ChannelType.PRIVATE,privateDto);
		System.out.println(channel1.getType()+ "채널 생성: "+ "채널 아이디" + channel1.getId());

		channelService.findChannel(channel.getId());
		System.out.println("---->각 체널 조회<----");
		System.out.println("PUBLIC채널: " + channel.getId());
		channelService.findChannel(channel1.getId());
		System.out.println("PRIVATE채널: " +channel1.getId());

		List<ChannelDto.FindDto> list=channelService.findAllByUserId(owner.getId());
		System.out.println("전체 체널 목록: " + list);

		ChannelDto.UpdateDto updateDto = new ChannelDto.UpdateDto(
				channel.getId(),
				"공지사항2",
				"공지사항2입니다."
		);
		channelService.update(ChannelType.PUBLIC,updateDto);
		ChannelDto.FindDto findChannel = channelService.findChannel(channel.getId());
		if(findChannel.name().equals("공지사항2")){
			System.out.println("업데이트 된 PUBLIC체널명: " + updateDto.newName());
			System.out.println("성공적으로 수정되었습니다.");

		}else{
			System.err.println("수정 실패하였습니다..");
		}

		return channel;
	}


	static Message messageCreateTest(MessageService messageService, Channel channel, User author,BinaryContentService binaryContentService) {
		System.out.println("====>메시지 테스트<====");

		MessageDto.CreateMessage createMessage= new MessageDto.CreateMessage(
				"안녕하세요",
				channel.getId(),
				author.getId(),
				null
		);
		System.out.println(createMessage.content()+"라는 메시지가 생성되었습니다");
		Message message = messageService.create(createMessage);

		System.out.println("===>(첨부파일포함) 메시지 테스트<===");
		BinaryContentDto.createDto fileDto =  new BinaryContentDto.createDto("사진파일입니다","image.jpg");
		BinaryContent fileList = binaryContentService.create(fileDto);
		System.out.println("메시지 첨부파일 생성완료:" + fileList.getId());

		List<UUID> attachmentList = List.of(fileList.getId());

		MessageDto.CreateMessage createMessage1 =new MessageDto.CreateMessage(
				"사진입니다.",
				channel.getId(),
				author.getId(),
				attachmentList
		);

		Message message1= messageService.create(createMessage1);



		System.out.println("메시지 생성: " + message.getContent());
		messageService.find(message.getId());
		System.out.println("단건 메시지 조회: " + message.getContent());
		List<Message> channelMessage=messageService.findAllByChannelId(channel.getId());
		System.out.println("채널속의 메시지:" + channelMessage);
		MessageDto.UpdateMessage updateDto= new MessageDto.UpdateMessage(
				message.getId(),
				"반가워요 ㅋ",
				null
		);
		Message updateMessage = messageService.update(updateDto);
		System.out.println("업데이트된 메시지: " + updateMessage);
		return message;
	}
	public static void main(String[] args) {
		ConfigurableApplicationContext context= SpringApplication.run(DiscodeitApplication.class,args);
		UserService userService=context.getBean(UserService.class);
		ChannelService channelService=context.getBean(ChannelService.class);
		MessageService messageService=context.getBean(MessageService.class);
		BasicAuthService basicAuthService=context.getBean(BasicAuthService.class);
		UserStatusService userStatusService = context.getBean(UserStatusService.class);
		BinaryContentService binaryContentService=context.getBean(BinaryContentService.class);

		User user = TestUser(userService,binaryContentService,basicAuthService,userStatusService);
		Channel channel = testChannel(channelService,user);
		Message message =messageCreateTest(messageService,channel,user,binaryContentService);

		messageService.delete(message.getId());
		System.out.println("메시지 삭제 완료:" + message.getId());
		channelService.delete(channel.getId());
		System.out.println("채널 삭제 완료:" + channel.getId());
		userService.delete(user.getId());
		System.out.println("유저 삭제 완료: " +user.getId());


















		}
	}
