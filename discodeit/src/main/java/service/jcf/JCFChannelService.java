package service.jcf;

import entity.Channel;
import service.ChannelService;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data = new ArrayList<>();

    @Override
    public void delete(UUID id) {
        for (Channel channel : data) {
            if(channel.getId().equals(id)) {
                data.remove(channel); // for문에서 remove쓰면 안되나?
                return; // ? 반환
            }
        }
        // data.removeIf(message -> message.getId().equals(id));
    }


    @Override
    public Channel find(UUID id) {
        // for문 : data에서
        for (Channel channel : data) {
            // if channel에 있는 getId의 값이 id값과 같으면
            if (channel.getId().equals(id))
                // 그 값을 channel에 반환해라
                return channel;
        }

        return null;
    }

    @Override
    public Channel create(String frame, String channelName, String detail) {
        // 기존의 생성자를 이용해서 객체 생성
        Channel newChannel = new Channel(frame, channelName, detail);
        // 리스트에 저장
        data.add(newChannel);
        // 생성된 객체 반환 -> null 금지 오류 남
        return newChannel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
        // return new ArrayList<>(data.value());
        // return data;
    }

    @Override
    public Channel update(UUID id, String frame, String channelName, String detail) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {

                channel.update(frame, channelName, detail);

                return channel;
            }
        }
        return null;
    }

}



