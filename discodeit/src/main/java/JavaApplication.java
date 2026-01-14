import Service.ChannelService;
import Service.MessageService;
import Service.UserService;

import Service.jcf.JCFChannelService;
import Service.jcf.JCFMessageService;
import Service.jcf.JCFUserService;

import entity.Channel;
import entity.Message;
import entity.User;
import exception.NotFoundException;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args)  {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService); /* 의존성 주입 (DI) */

        System.out.println("=== 1. CREATE (유저/채널/메시지 생성) ===");

        //유저 생성
        User user = userService.create("Seongjun", "seongjun@test.com", "010-1234-5678");
        System.out.println("유저 생성: " + user.getId() + " / " + user.getDisplayName()); //고유값이 필요한가?

        //채널 생성 (ownerId로 유저 연결)
        Channel channel = channelService.create("general", user.getId());
        System.out.println("채널 생성: " + channel.getId() + " / " + channel.getName()
                + " (소유주 = " + channel.getOwnerId() + ")"); // 소유주가 과연 필요한가

        //메시지 생성 (channelId + senderId 연결)
        System.out.println("\n=== 성공 ===");
        Message message = messageService.create(channel.getId(), user.getId(), "안녕하세요!");
        System.out.println("메세지 생성: " + message.getId() + " / " + message.getContent() //send도 호환 가능할까?
                + " (채널이름 = " + message.getChannelId() + ", 보낸사람 = " + message.getSenderId() + ")");

        /* DI 로직 검증 (실패 3, 성공 1)
        값 이상 = IllegalArgumentException
        값 정상인데 대상 없을 시 NotFoundException
         */
        System.out.println("\n=== 실패 1: 채널 없이 생성 ===");
        try {
            messageService.create(UUID.randomUUID(), user.getId(), "채널 없음");
        } catch (NotFoundException e) {
            System.out.println("expected error: " + e.getMessage());
        }

        System.out.println("\n=== 실패 2: 유저 없이 생성 ===");
        try {
            messageService.create(channel.getId(), UUID.randomUUID(), "유저 없음");
        } catch (NotFoundException e) {
            System.out.println("expected error: " + e.getMessage());
        }

        System.out.println("\n===실패 3: 내용 없이 생성 ==="); //null 조건문하고 같은 녀석
        try {
            messageService.create(channel.getId(), user.getId(), "");
        } catch (IllegalArgumentException e) { //빈 문자열은 애초에 허용안됨, 입력규칙 위반
            System.out.println("expected error: " + e.getMessage());
        }

        System.out.println("\n=== 2. READ (단건 조회) ===");

        User foundUser = userService.findById(user.getId());
        System.out.println("유저 찾기: " + (foundUser != null ? foundUser.getDisplayName() : "null"));

        Channel foundChannel = channelService.findById(channel.getId());
        System.out.println("채널 찾기: " + (foundChannel != null ? foundChannel.getName() : "null"));

        Message foundMessage = messageService.findById(message.getId());
        System.out.println("메세지 찾기: " + (foundMessage != null ? foundMessage.getContent() : "null"));

        System.out.println("\n=== 3. READ ALL (전체 조회) ===");
        System.out.println("유저목록: " + userService.findAll().size());
        System.out.println("채널목록: " + channelService.findAll().size());
        System.out.println("채팅목록: " + messageService.findAll().size());

        System.out.println("\n=== 4. 등록 여부 (등록 여부 boolean) ==="); // exitsById는 boolean을 사용
        System.out.println("User 등록: " + userService.exitsById(user.getId()));
        System.out.println("Channel 등록: " + channelService.exitsById(channel.getId()));
        System.out.println("Message 등록: " + messageService.existById(message.getId()));

        System.out.println("\n=== 5. UPDATE (수정) ==="); //먼가 먼가 다시 건드려봐야 할 듯

        userService.update(user.getId(), "Seongjun Yun", "seongjunyun@test.com", "010-0000-0000");
        System.out.println("수정된 User Name: " + userService.findById(user.getId()).getDisplayName());

        channelService.update(channel.getId(), "notice");
        System.out.println("수정된 Channel Name: " + channelService.findById(channel.getId()).getName());

        messageService.update(message.getId(), "수정된 메세지 내용~");
        System.out.println("수정된 Message Content: " + messageService.findById(message.getId()).getContent());

        System.out.println("\n=== 6. DELETE (삭제) ===");

        // 삭제는 “의존성 역순”이 안전 (메시지 → 채널 → 유저) // 유저의 데이터를 잃은 상태에서 채널과 메세지의 주소값이 엉킴
        messageService.delete(message.getId());
        channelService.delete(channel.getId());
        userService.delete(user.getId());

        System.out.println("삭제 후 Users: " + userService.findAll().size());
        System.out.println("삭제 후 Channels: " + channelService.findAll().size());
        System.out.println("삭제 후 Messages: " + messageService.findAll().size());

        System.out.println("\n=== 7. exits After delete(삭제 후 등록 여부) ===");
        System.out.println("User 등록 여부? " + userService.exitsById(user.getId()));
        System.out.println("Channel 등록 여부? " + channelService.exitsById(channel.getId()));
        System.out.println("Message 등록 여부? " + messageService.existById(message.getId()));

        System.out.println("\n=== DONE ===");
       }
    }
