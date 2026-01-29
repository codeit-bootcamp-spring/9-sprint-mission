package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    private final UserService userService;
    private final ChannelService channelService;

    //DI: 메시지는 유저/채널 존재 검증이 필요
    public FileMessageService(UserService userService, ChannelService channelService) {
        this.DIRECTORY = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                Message.class.getSimpleName()
        );
        this.userService = userService;
        this.channelService = channelService;
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

    // =========================
    // Public API
    // =========================

    @Override
    public Message create(UUID channelId, UUID senderId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }

        //관계 검증(심화 요구사항)
        if (!channelService.existsById(channelId)) {
            throw new NotFoundException("Channel not found id=" + channelId);
        }
        if (!userService.existsById(senderId)) {
            throw new NotFoundException("User not found=" + senderId);
        }

        Message message = new Message(channelId, senderId, content);
        write(resolvePath(message.getId()), message);
        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        return readOrThrow(messageId);
    }

    @Override
    public List<Message> findAll() {
        ensureDirectory();

        try (var stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::readPathAsMessage)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list directory: " + DIRECTORY, e);
        }
    }

    @Override
    public Message update(UUID messageId, String Content) {
        if (Content == null || Content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }

        Message message = readOrThrow(messageId);
        message.update(Content);
        write(resolvePath(message.getId()), message);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        deleteFileOrThrow(messageId);
    }

    @Override
    public boolean existsById(UUID messageId) {
        if (messageId == null) return false;
        return Files.exists(resolvePath(messageId));
    }

    // =========================
    // Helper Methods
    // =========================

    private Message readOrThrow(UUID messageId) {
        Message message = readOrNull(messageId);
        if (message == null) {
            throw new NotFoundException("Message not found id=" + messageId);
        }
        return message;
    }

    private Message readOrNull(UUID messageId) {
        if (messageId == null) return null;
        Path path = resolvePath(messageId);
        if (Files.notExists(path)) return null;
        return readPathAsMessage(path);
    }

    private Message readPathAsMessage(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (Message) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read message file: " + path, e);
        }
    }

    private void write(Path path, Message message) {
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(message);

        } catch (IOException e) {
            throw new RuntimeException("Failed to write message file: " + path, e);
        }
    }

    private void deleteFileOrThrow(UUID messageId) {
        Path path = resolvePath(messageId);
        if (Files.notExists(path)) {
            throw new NotFoundException("Message not found id=" + messageId);
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete message file: " + path, e);
        }
    }
}