package service.jcf;

import entity.Channel;
import entity.User;
import service.ChannelService;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data = new ArrayList<>();

    public JCFChannelService() { super();
    }
    /*public JCFUserService() {
        this.users = new ArrayList<>();*/

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Channel find(UUID id) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) return channel;
        }
        return null;
    }

    @Override
    public Channel create(String frame, String channelName, String detail) {
        // 기존의 생성자를 이용해서 객체 생성
        Channel newChannel = new Channel(frame, channelName, detail);
        // 리스트에 저장
        data.add(newChannel);
        // 생성된 객체 반환 -> null 금지 오류남
        return newChannel;
    }

    @Override
    public List<Channel> findAll() {
        return data;
        //return List.of(); // 무조건 빈 리스트만 반환함
    }

    @Override
    public Channel update(UUID id, String channelName, String detail) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                channel.setChannelName(channelName);
                channel.setDetail(detail);

                return channel;
            }
        }
        return null;
    }

}



