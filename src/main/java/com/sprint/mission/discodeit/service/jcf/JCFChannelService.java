package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.awt.*;
import java.util.*;
import java.util.List;

public class JCFChannelService implements ChannelService {

    private final JCFChannelRepository jcfChannelRepository;

    public JCFChannelService(JCFChannelRepository jcfChannelRepository) {
        this.jcfChannelRepository = jcfChannelRepository;
    }

    @Override
    public Channel create(String name, ChannelType type) {
        validateDuplicateName(name, null);
        Channel channel = new Channel(name, type);
        return jcfChannelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID channelId) {
        Channel channel = jcfChannelRepository.findById(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("채널 아이디 " + channelId + "는 존재하지않습니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return jcfChannelRepository.findAll();
    }

    @Override
    public Channel update(UUID channelId, String name) {
        Channel channel = findById(channelId);

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 비어 있을 수 없습니다.");
        }

        validateDuplicateName(name, channelId);

        channel.updateName(name);

        return jcfChannelRepository.update(channel);
    }

    private void validateDuplicateName(String name, UUID id) {
        Optional<Channel> channel = jcfChannelRepository.findByName(name);
        if (channel.isPresent()) {
            if (id == null || !id.equals(channel.get().getId())) {
                throw new IllegalArgumentException("이미 사용 중인 채널명입니다.");
            }
        }
    }

    @Override
    public void delete(UUID channelId) {
        findById(channelId);
        jcfChannelRepository.delete(channelId);
    }
}
