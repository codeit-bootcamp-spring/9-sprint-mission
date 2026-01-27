package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final FileChannelRepository fileChannelRepository;

    public FileChannelService(FileChannelRepository fileChannelRepository) {
        this.fileChannelRepository = fileChannelRepository;
    }

    @Override
    public Channel create(String name, ChannelType type) {
        Channel channel = new Channel(name, type);
        return fileChannelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = fileChannelRepository.findById(id);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return fileChannelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = findById(id);

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 비어 있을 수 없습니다.");
        }

        channel.updateName(name);
        return fileChannelRepository.update(channel);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        fileChannelRepository.delete(id);
    }
}
