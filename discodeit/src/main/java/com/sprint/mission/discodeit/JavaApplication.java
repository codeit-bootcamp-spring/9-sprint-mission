package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        // User
        // 서비스 객체 생성
        UserService userService = new JCFUserService();

        // 사용자 생성
        User user1 = userService.create("Alice", "abc@gmail.com");
        User user2 = userService.create("Bob","example@gmail.com");

        // 단건 조회
        User foundUser = userService.findById(user1.getId());
        System.out.println("사용자 단건 조회: "+foundUser.getName()+','+foundUser.getEmail());

        // 전체 조회
        List<User> users = userService.findAll();
        System.out.println("전체 사용자 조회: 총 "+users.size()+"명");
        users.forEach(user -> System.out.println(user.getName()+','+user.getEmail()));

        // 사용자 수정 시간 검증
        long beforeUserUpdatedAt = System.currentTimeMillis();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 수정
        User updatedUser = userService.update(user2.getId(), "Sophia","abcd@gmail.com");
        System.out.println("사용자 수정 후 조회: "+updatedUser.getName()+','+updatedUser.getEmail());

        long afterUserUpdatedAt = updatedUser.getUpdatedAt();

        System.out.println("사용자 수정 전 시간: " + beforeUserUpdatedAt);
        System.out.println("사용자 수정 후 시간: " + afterUserUpdatedAt);
        System.out.println("수정 시간 변경 여부: " +
                (afterUserUpdatedAt > beforeUserUpdatedAt));

        // 삭제
        userService.delete(user2.getId());
        System.out.println("사용자 삭제 후 조회:");
        userService.findAll().forEach(user -> System.out.println(user.getName()));

        // Channel
        // 서비스 객체 생성
        ChannelService channelService = new JCFChannelService();

        // 채널 생성
        Channel channel1 = channelService.create("public");
        Channel channel2 = channelService.create("private");

        // 채널 조회
        Channel foundChannel = channelService.findById(channel1.getId());
        System.out.println("채널 조회: "+foundChannel.getName());

        // 채널 전체 조회
        List<Channel> channels = channelService.findAll();
        System.out.println("전체 채널 조회: 총"+channels.size()+"명");
        channels.forEach(channel -> System.out.println(channel.getName()));

        // 채널 수정 시간 검증
        long  beforeChannelUpdatedAt = foundChannel.getUpdatedAt();

        try{
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 채널 이름 수정
        Channel channelUpdater = channelService.update(channel2.getId(), "public2");
        System.out.println("채널 이름 수정 후 조회: "+channelUpdater.getName());

        long afterChannelUpdatedAt = channelUpdater.getUpdatedAt();

        System.out.println("채널 수정 전 시간: " + beforeChannelUpdatedAt);
        System.out.println("채널 수정 후 시간:  " + afterChannelUpdatedAt);
        System.out.println("수정 시간 변경 여부: "+
                (afterChannelUpdatedAt > beforeChannelUpdatedAt));

        // 채널 삭제
        channelService.delete(channel2.getId());
        System.out.println("채널2 삭제 후 조회: ");
        channelService.findAll().forEach(c -> System.out.println(c.getName()));

        // Message
        // 서비스 객체 생성
        MessageService messageService =
                new JCFMessageService(userService, channelService);

        // 메시지 생성
        Message message1 = messageService.create(
                channel1.getId(),
                user1.getId(),
                "First Message"
        );

        Message message2 = messageService.create(
                channel1.getId(),
                user1.getId(),
                "Second Message"
        );

        // 단건 조회
        Message foundMessage = messageService.findById(message1.getId());
        System.out.println("메시지 단건 조회: " + foundMessage.getContent());

        // 채널별 메시지 조회
        List<Message> channelMessages = messageService.findByChannelId(channel1.getId());
        System.out.println("채널 메시지 조회: 총 " + channelMessages.size() + "개");
        channelMessages.forEach(m -> System.out.println(m.getContent()));

        // 보낸 사람 기준 조회
        List<Message> senderMessages = messageService.findBySenderId(user1.getId());
        System.out.println("보낸 메시지 조회: 총 " + senderMessages.size() + "개");
        senderMessages.forEach(m -> System.out.println(m.getContent()));

        // 메시지 수정 시간 검증
        long beforeMessageUpdatedAt = message1.getUpdatedAt();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 메시지 수정
        Message updatedMessage =
                messageService.update(message1.getId(), "Hello Updated!");
        System.out.println("메시지 수정 후: " + updatedMessage.getContent());

        long afterMessageUpdatedAt = updatedMessage.getUpdatedAt();

        System.out.println("메시지 수정 전 시간: " + beforeMessageUpdatedAt);
        System.out.println("메시지 수정 후 시간: " + afterMessageUpdatedAt);
        System.out.println("수정 시간 변경 여부: " +
                (afterMessageUpdatedAt > beforeMessageUpdatedAt));

        // 메시지 삭제
        messageService.delete(message2.getId());
        System.out.println("메시지 삭제 후 조회:");
        messageService.findByChannelId(channel1.getId())
                .forEach(m -> System.out.println(m.getContent()));

        // 의존성 검증
        User user = userService.create("Alice", "a@gmail.com");
        Channel channel = channelService.create("general");

        Message message = messageService.create(
                channel.getId(),
                user.getId(),
                "Hello!"
        );
        System.out.println(message.getContent());
    }

}
