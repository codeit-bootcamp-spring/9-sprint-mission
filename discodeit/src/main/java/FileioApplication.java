import entity.Channel;
import entity.Message;
import entity.User;
import service.ChannelService;
import service.MessageService;
import service.UserService;
import service.file.FileChannelService;
import service.file.FileMessageService;
import service.file.FileUserService;

import java.util.List;

public class FileioApplication {
    static void userCRUDTest(UserService userService) {
        User user = new User("전승현", "asdadas", "12313");
        userService.addUser(user);
        System.out.println("유저 생성: " + user.getId());

        User foundUser = userService.getUser(user.getUsername());
        if (foundUser != null) {
            System.out.println("유저 조회(단건): " + foundUser.getId());
        }

        List<User> foundUsers = userService.getAllUsers();
        System.out.println("유저 조회(다건): " + foundUsers.size());

        System.out.println("유저 수정 시도..");
        user.update("전승현", "new@email.com", "010-1234-5678");
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
        User user = new User("woody", "woody@codeit.com", "woody1234");
        userService.addUser(user);
        return user;
    }

    static Channel setupChannel(ChannelService channelService, User owner) {
        try {
            Channel channel = channelService.createChannel("공지", owner);
            return channel;
        } catch (Exception e) {
            System.out.println("채널이 존재합니다.");
        }
        return null;
    }


    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = new Message("안녕하세요.", author, author);
        messageService.sendMessage(message);
        System.out.println("메시지 생성 확인: " + message.getContent());
    }

    public static void main(String[] args) {
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService(channelService,userService);

        try {
            System.out.println("--- User Test ---");
            userCRUDTest(userService);


            User owner = setupUser(userService);
            User guest = new User("guest", "guest@test.com", "1111");
            userService.addUser(guest);
            Channel channel = setupChannel(channelService, owner);

            System.out.println("\n--- Channel Test ---");
            channelCRUDTest(channelService, owner);

            System.out.println("\n--- Message Test ---");
            messageCRUDTest(messageService, owner, guest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}