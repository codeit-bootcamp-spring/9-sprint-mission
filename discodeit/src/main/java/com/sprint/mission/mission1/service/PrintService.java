package com.sprint.mission.mission1.service;

import com.sprint.mission.mission1.entity.Channel;
import com.sprint.mission.mission1.entity.Message;
import com.sprint.mission.mission1.entity.User;

import java.util.List;

public class PrintService {

    public void printChannel(Channel channel) {
        System.out.println("\n========" + channel + "========");
        System.out.println("UID: " + channel.getId());
        System.out.println("채널이름: " + channel.getName());
        System.out.println("소유자: " + channel.getOwnerId());
        System.out.println("생성일자: " + channel.getCreatedAt());
        System.out.println("수정일자: " + channel.getUpdatedAt());
        System.out.println("========" + channel + "========\n");
    }
    public void printChannel(List<Channel> channels) {
        for (Channel channel : channels) {
            System.out.println("\n========" + channel + "========");
            System.out.println("UID: " + channel.getId());
            System.out.println("채널이름: " + channel.getName());
            System.out.println("소유자: " + channel.getOwnerId());
            System.out.println("생성일자: " + channel.getCreatedAt());
            System.out.println("수정일자: " + channel.getUpdatedAt());
            System.out.println("========" + channel + "========\n");
        }
    }

    public void printUser(User user) {
        System.out.println("\n========" + user + "========");
        System.out.println("UID: " + user.getId());
        System.out.println("이름: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("전화번호: " + user.getEmail());
        System.out.println("생성일자: " + user.getCreatedAt());
        System.out.println("수정일자: " + user.getUpdatedAt());
        System.out.println("========" + user + "========\n");
    }
    public void printUser(List<User> users) {
        for (User user : users) {
            System.out.println("\n========" + user + "========");
            System.out.println("UID: " + user.getId());
            System.out.println("이름: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("전화번호: " + user.getEmail());
            System.out.println("생성일자: " + user.getCreatedAt());
            System.out.println("수정일자: " + user.getUpdatedAt());
            System.out.println("========" + user + "========\n");
        }
    }

    public void printMessage(Message message) {
        System.out.println("\n========" + message + "========");
        System.out.println("UID: " + message.getId());
        System.out.println("내용: " + message.getContent());
        System.out.println("채널: " + message.getChannelId());
        System.out.println("작성자: " + message.getUserId());
        System.out.println("작성일자: " + message.getCreatedAt());
        System.out.println("수정일자: " + message.getUpdatedAt());
        System.out.println("========" + message + "========\n");
    }
    public void printMessage(List<Message> messages) {
        for (Message message : messages) {
            System.out.println("\n========" + message + "========");
            System.out.println("UID: " + message.getId());
            System.out.println("내용: " + message.getContent());
            System.out.println("채널: " + message.getChannelId());
            System.out.println("작성자: " + message.getUserId());
            System.out.println("작성일자: " + message.getCreatedAt());
            System.out.println("수정일자: " + message.getUpdatedAt());
            System.out.println("========" + message + "========\n");
        }
    }

}
