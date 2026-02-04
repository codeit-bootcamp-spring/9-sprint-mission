import entity.Channel;
import entity.Message;
import entity.User;
import org.w3c.dom.ls.LSOutput;
import repository.ChannelRepository;
import repository.MessageRepository;
import repository.UserRepository;
import repository.file.FileChannelRepository;
import repository.file.FileMessageRepository;
import repository.file.FileUserRepository;
import service.ChannelService;
import service.MessageService;
import service.UserService;
import service.basic.BasicChannelService;
import service.basic.BasicMessageService;
import service.basic.BasicUserService;
import service.file.FileChannelService;
import service.file.FileMessageService;
import service.file.FileUserService;

import java.util.List;
import java.util.NoSuchElementException;

public class FileioApplication {
    static void userCRUDTest(UserService userService) {
        User user = new User("전승현", "asdadas", "12313");

        userService.addUser(user);
        System.out.println("유저 아이디:  " + user.getId()+ "\n유저 이름: "+user.getUsername());
        System.out.println("====> 유저 생성 완료 < =====");

        User foundUser = userService.getUser(user.getUsername());
        if (foundUser != null) {
            System.out.println("유저 조회(단건): " + foundUser.getUsername());
        }

        List<User> foundUsers = userService.getAllUsers();
        System.out.println("유저 조회(다건): " + foundUsers.get(0).getUsername()+" "+foundUsers.get(1).getUsername()+ " " + foundUsers.get(2).getUsername());

        System.out.println("유저 수정 시도..");
        user.update("전승현", "ㅇㄴㄹㅇㄴ", "ㄴㄹㅇㄴㄹ");
        userService.updateUser(user);

        System.out.println("유저 수정 완료: " + String.join("/",
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber()));

        userService.deleteUser(user.getUsername());
        List<User> foundUsersAfterDelete = userService.getAllUsers();
        System.out.println("유저 삭제 후 : " + foundUsersAfterDelete.size());
    }

    static void channelCRUDTest(ChannelService channelService, User loginUser) {
        Channel channel = channelService.createChannel("공지사항", loginUser);
        if (channel == null) return;
        System.out.println("채널 생성 확인: " + channel.getName());

        Channel foundChannel = channelService.findChannel("공지사항");
        if (foundChannel != null) {
            System.out.println("채널 조회 성공: " + foundChannel.getName());
        }

        List<Channel> foundChannels = channelService.AllChannels();
        System.out.println("채널 조회(다건): " + foundChannels.size());

        Channel updatedChannel = channelService.changeChannel(foundChannel, "공지사항_수정", loginUser);
        if (updatedChannel != null) {
            System.out.println("채널 수정 완료: " + updatedChannel.getName());
        }

        boolean isDeleted = channelService.channelRemove(foundChannel, loginUser);
        System.out.println("채널 삭제 결과: " + isDeleted);

        List<Channel> foundChannelsAfterDelete = channelService.AllChannels();
        System.out.println("삭제 후 채널 수: " + foundChannelsAfterDelete.size());
    }

    static void messageCRUDTest(MessageService messageService, User sender, User receiver) {
        Message message = new Message("안녕하세요.", sender, receiver);
        messageService.sendMessage(message);
        System.out.println("메시지 전송 완료: " + message.getContent());

        List<Message> allMessages = messageService.getMessages();
        if (allMessages != null) {
            System.out.println("전체 메시지 조회(다건): " + allMessages.size());
        }

        List<Message> received = messageService.getReceiverMessages(receiver);
        System.out.println(receiver.getUsername() + "님의 받은 메시지 수: " + received.size());

        try{boolean isDeleted = messageService.deleteMessage("안녕하세요.", sender);
        System.out.println("메시지 삭제 결과: " + isDeleted);}catch(Exception e){
            System.out.println("메시지가 존재하지 않습니다.");
        }

        List<Message> afterDelete = messageService.getMessages();
        int count = (afterDelete == null) ? 0 : afterDelete.size();
        System.out.println("삭제 후 잔여 메시지 수: " + count);
    }

    static User setupUser(UserService userService) {
        User user = new User("전팝콘", "ㅁㄴㅇㅁㄴㅇ", "ㅁㄴㅇ");
        userService.addUser(user);
        return user;
    }






    public static void main(String[] args) {
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService(channelService,userService);

        try {
            System.out.println("--- User Test ---");


            User owner = setupUser(userService);
            User guest = new User("앨리", "ㅁㄴㅇ", "1111");
            userService.addUser(guest);
            userCRUDTest(userService);



            System.out.println("\n--- Channel Test ---");
            channelCRUDTest(channelService, owner);

            System.out.println("\n--- Message Test ---");
            messageCRUDTest(messageService, owner, guest);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n----> 심화 < ----\n");
        User user = new User("전승현","ㅁㄴㅇ","ㅁㄴㅇ");
        User user1 =  new User("전팝콘" , "ㅁㅇㄴ","ㅁㄴㅇ");
        UserRepository userRepository = new FileUserRepository();
        UserService userService1 = new BasicUserService(userRepository);

        try {
            userService1.addUser(user);
            userService1.addUser(user1);
            System.out.println("추가 성공");
        }catch(IllegalStateException e){
            System.out.println(e.getMessage());
        }

        try{
            User found = userService1.getUser("전승현");
            System.out.println("조회 성공: "  + found.getUsername());
            User found1 = userService1.getUser("asd"); // 회원이 없을시 테스트
        }catch(NoSuchElementException e){
            System.out.println("조회 실패: " + e.getMessage());
        }

        userService1.getAllUsers();
        System.out.println("전체 회원 목록: " + userService1.getAllUsers().size());

        userService1.updateUser(user);

        System.out.println("-->수정<--");
        userService1.updateUser(user1);
        System.out.println("수정 " + userService1.updateUser(user));

        try{
            userService1.deleteUser("전팝콘");
            System.out.println("삭제성공");
            System.out.println("삭제 후 전체 조회:" + userService1.getAllUsers().size());

        }catch(NoSuchElementException e){
            System.out.println("회원이 없음");
        }

        System.out.println("\n ---->채널 <----\n");

        ChannelRepository channelRepository = new FileChannelRepository();
        ChannelService channelService1 = new BasicChannelService(channelRepository);

        Channel channel =null;

        try{
           channel = channelService1.createChannel("공습경보",user);
            System.out.println("채널 생성 완료! 체널이름: " + channel.getName()+" 채널 방장: " + channel.getOwner().getUsername());
        }catch(IllegalArgumentException e){
            System.out.println("생성 실패: " + e.getMessage());
        }

        try{
            channel = channelService1.findChannel("공습경보");
            System.out.println("찾은 채널 이름: " + channel.getName());
        }catch(NoSuchElementException e){
            System.out.println("검색 오류: " + e.getMessage());
        }
        if(channel != null){
            channelService1.changeChannel(channel,"공습경보 2",user);
            System.out.println("변경 완료: " + channel.getName());

        }
        if(channel != null){
           try {
               channelService1.addUser(channel, user1);
           }catch(NoSuchElementException e){
               System.out.println("추가 실패 " + e.getMessage());
           }
        }
        channelService1.AllChannels();
        if(channel != null) {
            try {
                channelService1.channelRemove(channel, user);
            }catch(IllegalStateException e){
                System.out.println("삭제실패 " + e.getMessage());
            }
        }

        System.out.println("\n---->메시지 테스트<----");
        MessageRepository messageRepository = new FileMessageRepository(channelService1, userService1);
        MessageService messageService1 = new BasicMessageService(messageRepository, userService1, channelService1);

        try {

            Message message = new Message("ㅎㅇ", user, user1);


            messageService1.sendMessage(message);
            System.out.println("메시지 전송 완료! 내용: " + message.getContent());


            List<Message> receivedMessages = messageService1.getReceiverMessages(user1);
            System.out.println(user1.getUsername() + "님의 수신 메시지 개수: " + receivedMessages.size());

            if (!receivedMessages.isEmpty()) {
                System.out.println("최근 받은 메시지: " + receivedMessages.get(0).getContent());
            }

        } catch (NullPointerException e) {
            System.out.println("전송 실패: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("시스템 오류: " + e.getMessage());
        }



    }
}



