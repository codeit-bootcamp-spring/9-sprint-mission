package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    private final ReadStatusRepository readStatusRepository;

    public FileChannelService(ReadStatusRepository readStatusRepository) {
        this.readStatusRepository = readStatusRepository;
        this.DIRECTORY = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                Channel.class.getSimpleName()
        );
        ensureDirectory();
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + DIRECTORY, e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    @Override
    public Channel createPublic(PublicChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("channel name must not be blank");
        }
        if (request.ownerId() == null) {
            throw new IllegalArgumentException("ownerId must not be null");
        }

        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.ownerId(), request.description());
        write(resolvePath(channel.getId()), channel);
        return channel;
    }

    @Override
    public Channel createPrivate(PrivateChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.ownerId() == null) {
            throw new IllegalArgumentException("ownerId must not be null");
        }
        if (request.participantUserIds() == null || request.participantUserIds().isEmpty()) {
            throw new IllegalArgumentException("participantUserIds must not be empty");
        }

        // PRIVATE 채널은 name/description을 생략한다.
        Channel channel = new Channel(ChannelType.PRIVATE, null, request.ownerId(), null);
        write(resolvePath(channel.getId()), channel);

        // 참여자(요청에 포함된 유저 + owner)별 ReadStatus 생성
        Set<UUID> memberIds = new HashSet<>(request.participantUserIds());
        memberIds.add(request.ownerId());

        Instant now = Instant.now();
        for (UUID userId : memberIds) {
            if (readStatusRepository.findByUserIdAndChannelId(userId, channel.getId()).isPresent()) {
                continue;
            }
            ReadStatus readStatus = new ReadStatus(UUID.randomUUID(), userId, channel.getId(), now);
            readStatusRepository.save(readStatus);
        }

        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return readOrThrow(channelId);
    }

    @Override
    public List<Channel> findAll() {
        ensureDirectory();

        try (var stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::readPathAsChannel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list directory: " + DIRECTORY, e);
        }
    }

    @Override
    public Channel update(UUID channelId, String description, String name) {
        Channel channel = readOrThrow(channelId);
        channel.update(description, name);
        write(resolvePath(channel.getId()), channel);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        deleteFileOrThrow(channelId);
    }

    @Override
    public boolean existsById(UUID channelId) {
        if (channelId == null) return false;
        return Files.exists(resolvePath(channelId));
    }


    private Channel readOrThrow(UUID channelId) {
        Channel channel = readOrNull(channelId);
        if (channel == null) {
            throw new NotFoundException("Channel not found id=" + channelId);
        }
        return channel;
    }

    private Channel readOrNull(UUID channelId) {
        if (channelId == null) return null;
        Path path = resolvePath(channelId);
        if (Files.notExists(path)) return null;
        return readPathAsChannel(path);
    }

    private Channel readPathAsChannel(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (Channel) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read channel file: " + path, e);
        }
    }

    private void write(Path path, Channel channel) {
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(channel);

        } catch (IOException e) {
            throw new RuntimeException("Failed to write channel file: " + path, e);
        }
    }

    private void deleteFileOrThrow(UUID channelId) {
        Path path = resolvePath(channelId);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete channel file: " + path, e);
        }
    }
}