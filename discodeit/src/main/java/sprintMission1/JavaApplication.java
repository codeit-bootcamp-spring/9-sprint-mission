package sprintMission1;

import sprintMission1.entity.Channel;
import sprintMission1.entity.Message;
import sprintMission1.entity.User;
import sprintMission1.service.ChannelService;
import sprintMission1.service.MessageService;
import sprintMission1.service.UserService;
import sprintMission1.service.jcf.JCFChannelService;
import sprintMission1.service.jcf.JCFMessageService;
import sprintMission1.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        System.out.println("\n------------유저-----------------");

        //유저 생성
        User user1 = userService.create("김대성");
        User user2 = userService.create("박성호");

        //단건 조회
        userService.read(user1.getId());

        //다건 조회
        userService.readAll();

        //이름 변경
        userService.update(user1.getId(), "나 김대성 아니다");

        //변경 확인
        userService.read(user1.getId());

        //제거
        userService.delete(user1.getId());

        System.out.println("\n------------채널-----------------");

        //채널 생성
        Channel channel1 = channelService.create(user2, "성호님의 채널");
        Channel channel2 = channelService.create(user1, "대성님의 채널");

        //단건 조회
        channelService.read(channel1.getId());

        //다건 조회
        channelService.readAll();

        //제거
        channelService.delete(channel1.getId());

        //변경
        channelService.update(channel2.getId(), "바꿈");

        //변경 확인
        channelService.readAll();

        System.out.println("\n------------메세지-----------------");

        //메세지 생성
        Message message1 = messageService.create("가나다라마바사", user1, channel2);
        Message message2 = messageService.create("아자차카타파하", user2, channel2);

        //단건 조회
        messageService.read(message1.getId());
        messageService.read(message2.getId());

        //다건 조회
        messageService.readAll();

        //제거
        messageService.delete(message1.getId());

        //수정
        messageService.update(message2.getId(), "ABCDEFG");

        //수정 확인
        messageService.read(message2.getId());
    }
}
