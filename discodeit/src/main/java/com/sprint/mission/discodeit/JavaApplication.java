package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelSevice;
import com.sprint.mission.discodeit.service.MessageSevice;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelSevice channelSevice = new JCFChannelService();
        MessageSevice messageSevice = new JCFMessageService();
        UUID admin = UUID.randomUUID();
        UUID admin1 = UUID.randomUUID();
        UUID userUUID = UUID.randomUUID();
        UUID userUUID1 = UUID.randomUUID();
//==========================================유저=============================================================
        User user = userService.create("정혁조", "dy960508@naver.com", "010-1234-5678");
        User user1 = userService.create("복슬이", "ddochy7777@gmail.com", "010-9876-5432");
        System.out.println("유저 생성: " + user.getName());

        User foundUser = userService.find(user.getId());
        System.out.println("단건 조회: " + foundUser);

        List<User> users = userService.findAll();
        System.out.println("다건 조회: " + users);

        User update = userService.update(user.getId(), "혁조", "dy960508@naver.com", "010-1234-5678");
        if (update != null) {
            System.out.println("수정된 유저: " + update);
        } else {
            System.out.println("변경된 값 또는 유저가 없습니다.");
        }

        userService.delete(user.getId());
        System.out.println("유저 삭제 완료");
        try {
            userService.find(user.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("삭제된 유저입니다.");
        }
//==========================================채널=============================================================
        Channel channel = channelSevice.create("Codeit", "Sprint", admin);
        Channel channel1 = channelSevice.create("Codeit1", "Study", admin1);
        System.out.println("채널 생성: " + channel.getDisplayname());

        Channel foundChannel = channelSevice.find(channel.getId());
        System.out.println("채널 조회: " + foundChannel.getDisplayname());

        List<Channel> channels = channelSevice.findAll();
        System.out.println("다건 조회:  " + channels);

        Channel updatedChannel =
                channelSevice.updateDisplayName(
                        channel.getId(),
                        admin,
                        "Java"
                );
        System.out.println("수정된 채널: " + updatedChannel);

        channelSevice.delete(channel.getId(), channel.getAdmin());
        System.out.println("채널 삭제 완료");
        try {
            channelSevice.find(channel.getAdmin());
        } catch (IllegalArgumentException e) {
            System.out.println("삭제된 채널입니다.");
        }
//==========================================메세지=============================================================
        Message message = messageSevice.create("정혁조", "안녕하세요.", userUUID);
        Message message1 = messageSevice.create("복슬이", "멍멍", userUUID1);
        System.out.println("메세지가 생성되었습니다. " + message);
        System.out.println("메세지가 생성되었습니다. " + message1);

        Message foundMessage = messageSevice.find(message.getUser());
        System.out.println("메세지 조회: " + foundMessage);

        List<Message> messages = messageSevice.findAll();
        System.out.println("메세지 전체 조회: " + messages);

        Message updateMessage = messageSevice.updateMessage(userUUID, "수정되었습니다.");
        System.out.println("메세지 수정 성공: " + updateMessage);

        messageSevice.delete(message.getUser());
        System.out.println("메세지 삭제 완료");
    }
}
