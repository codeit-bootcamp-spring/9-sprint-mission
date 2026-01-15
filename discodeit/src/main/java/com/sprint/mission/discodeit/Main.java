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

public class Main {
    public static void main(String[] args) {
        System.out.println("=== 🚀 디스코드 앱 테스트 시작 ===");

        // 1. 서비스(요리사) 준비
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        // 2. 유저 생성 (회원가입)
        // User 생성자: (이메일, 닉네임, 비번)
        User user = new User("kim@code.com", "김코딩", "1234");
        User savedUser = userService.createUser(user);
        System.out.println("✅ 유저 가입 완료: " + savedUser.getNickname());

        // 3. 채널 생성
        // Channel 생성자: (이름, 설명)
        Channel channel = new Channel("자유게시판", "잡담 환영");
        Channel savedChannel = channelService.createChannel(channel);
        System.out.println("✅ 채널 생성 완료: " + savedChannel.getChannelName());

        // 4. 메시지 작성
        // Message 생성자: (내용, 작성자ID, 채널ID)
        Message msg = new Message("안녕하세요! 첫 글입니다.", savedUser.getId(), savedChannel.getId());
        messageService.createMessage(msg);
        System.out.println("✅ 메시지 전송 완료");

        // 5. 전체 메시지 조회 및 검증
        System.out.println("\n--- 📜 전체 메시지 목록 ---");
        List<Message> allMessages = messageService.getAllMessages();

        for (Message m : allMessages) {
            System.out.println("내용: " + m.getContent());
            System.out.println("작성자 ID: " + m.getUserId());
            System.out.println("-> 작성자 일치 확인: " + m.getUserId().equals(savedUser.getId()));
        }

        System.out.println("\n=== 🎉 테스트 종료 ===");
    }
}
