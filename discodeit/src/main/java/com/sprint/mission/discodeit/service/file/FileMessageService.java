//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.service.MessageService;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//import java.util.UUID;
//
//public class FileMessageService implements MessageService {
//
//    private final Path DIRECTORY;
//    private final String EXTENSION = ".ser";
//
//    public FileMessageService() {
//        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
//        if (Files.notExists(DIRECTORY)) {
//            try {
//                Files.createDirectories(DIRECTORY);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    private Path resolvePath(UUID id) {
//        return DIRECTORY.resolve(id + EXTENSION);
//    }
//
//    @Override
//    public Message create(UUID channelId, UUID writerId, String content) {
//        Message message = new Message(channelId, writerId, content);
//        Path path = resolvePath(message.getId());
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
//
//    @Override
//    public void remove(UUID id) {
//        Path path = resolvePath(id);
//        if (Files.notExists(path)) {
//            throw new NoSuchElementException("Message with id " + id + " not found");
//        }
//        try {
//            Files.delete(path);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Message findByID(UUID id) {
//        Message message = null;
//        Path path = resolvePath(id);
//        if (Files.exists(path)) {
//            try (
//                    FileInputStream fis = new FileInputStream(path.toFile());
//                    ObjectInputStream ois = new ObjectInputStream(fis)
//            ) {
//                message = (Message) ois.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        return Optional.ofNullable(message)
//                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
//    }
//
//    @Override
//    public List<Message> getAll() {
//        try {
//            return Files.list(DIRECTORY)
//                    .filter(path -> path.toString().endsWith(EXTENSION))
//                    .map(path -> {
//                        try (
//                                FileInputStream fis = new FileInputStream(path.toFile());
//                                ObjectInputStream ois = new ObjectInputStream(fis)
//                        ) {
//                            return (Message) ois.readObject();
//                        } catch (IOException | ClassNotFoundException e) {
//                            throw new RuntimeException(e);
//                        }
//                    })
//                    .toList();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Message updateContent(UUID id, String newContent) {
//        Message msgNullable = null;
//        Path path = resolvePath(id);
//        if (Files.exists(path)) {
//            try (
//                    FileInputStream fis = new FileInputStream(path.toFile());
//                    ObjectInputStream ois = new ObjectInputStream(fis)
//            ) {
//                msgNullable = (Message) ois.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        Message message = Optional.ofNullable(msgNullable)
//                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
//        message.updateContent(newContent);
//
//        try(
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
//}
