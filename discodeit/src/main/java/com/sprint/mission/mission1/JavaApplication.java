package com.sprint.mission.mission1;

import com.sprint.mission.mission1.entity.Channel;
import com.sprint.mission.mission1.entity.Message;
import com.sprint.mission.mission1.entity.User;
import com.sprint.mission.mission1.service.ChannelService;
import com.sprint.mission.mission1.service.MessageService;
import com.sprint.mission.mission1.service.PrintService;
import com.sprint.mission.mission1.service.UserService;
import com.sprint.mission.mission1.service.jcf.JCFChannelService;
import com.sprint.mission.mission1.service.jcf.JCFMessageService;
import com.sprint.mission.mission1.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        MessageService messageService = new JCFMessageService();
        ChannelService channelService = new JCFChannelService();
        PrintService printService = new PrintService();

        User user1 = userService.create("김대성", "codeit@gmail.com", "010-1234-5678");
        User user2 = userService.create("김대성2", "codeit2@gmail.com", "020-1234-5678");
        printService.printUser(userService.read(user1.getId()));
        printService.printUser(userService.readAll());
        userService.update(user1.getId(), "김대성3" ,"codeit2@gmail.com", "020-1234-5678");
        printService.printUser(userService.read(user1.getId()));
        userService.delete(user1.getId());

        Channel channel1 = channelService.create("1채널" , user2.getId());
        Channel channel2 = channelService.create("2채널" , user2.getId());
        printService.printChannel(channelService.read(channel1.getId()));
        printService.printChannel(channelService.readAll());
        channelService.update(channel1.getId(), "3채널");
        printService.printChannel(channelService.read(channel1.getId()));
        channelService.delete(channel1.getId());


        Message message1 = messageService.create(channel2.getId() , user2.getId(), "메세지1");
        Message message2 = messageService.create(channel2.getId() , user2.getId(), "메세지2");
        printService.printMessage(messageService.read(message1.getId()));
        printService.printMessage(messageService.readAll());
        messageService.update(message1.getId(), "메세지3");
        printService.printMessage(messageService.read(message1.getId()));
        messageService.delete(message1.getId());
    }
}
