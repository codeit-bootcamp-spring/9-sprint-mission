package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.DTO.MessageDTO;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService{
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageService(ChannelService channelService, UserService userService) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);}

//    @Override
//    public Message createMessage(String text, UUID channelId, UUID authorId){
//        Message message = new Message(authorId, channelId, text);
//        Path path = resolvePath(message.getMessageId());
//        try (
//                FileOutputStream fos = new FileOutputStream(path.toFile());
//                ObjectOutputStream oos = new ObjectOutputStream(fos)
//        ) {
//            oos.writeObject(message);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return message;
//    }

    @Override
    public Message createMessage(MessageDTO.CreateMessageDTO dto) {
        Message message = new Message(dto.authorId(), dto.channelId(), dto.text(), dto.attachmentIds());
        Path path = resolvePath(message.getMessageId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return message;
    }

    @Override
    public Message findByMessageId(UUID messageId){
        Message messageNullable = null;
        Path path = resolvePath(messageId);
        if (Files.notExists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                return (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        return Optional.ofNullable(messageNullable)
                .orElseThrow(() -> new NoSuchElementException("Message with messageId " + messageId + " not found"));

    }

    @Override
    public Message findByChannelId(UUID channelId) {
        Message messageNullable = null;
        Path path = resolvePath(channelId);
        if (Files.notExists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                return (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        return Optional.ofNullable(messageNullable)
                .orElseThrow(() -> new NoSuchElementException("Message with channelId " + channelId + " not found"));

    }

    @Override
    public List<Message> findAllMessage() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            System.out.println(path);
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message updateMessage(UUID messageId, String text) {
        Message messageNullable = null;
        Path path = resolvePath(messageId);
        if (Files.notExists(path)) {
            try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                messageNullable = (Message) ois.readObject();
            }catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        Message message = Optional.ofNullable(messageNullable)
                .orElseThrow(() -> new NoSuchElementException("Message with id" + messageId + " nod found"));
        message.updateMessage(text);

        try(
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ){
            oos.writeObject(message);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public boolean deleteMessage(UUID messageId) {
        Path path = resolvePath(messageId);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
     return true;
    }




}
