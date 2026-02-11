//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.ChannelType;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
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
//public class FileChannelService implements ChannelService {
//
//    private final Path DIRECTORY;
//    private final String EXTENSION = ".ser";
//
//    public FileChannelService() {
//        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
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
//    public Channel create(ChannelType type, String name) {
//        Channel channel = new Channel(type, name);
//        Path path = resolvePath(channel.getId());
//        try (
//                FileOutputStream fos = new FileOutputStream(path.toFile());
//                ObjectOutputStream oos = new ObjectOutputStream(fos)
//        ) {
//            oos.writeObject(channel);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return channel;
//    }
//
//    @Override
//    public void remove(UUID id) {
//        Path path = resolvePath(id);
//        if (Files.notExists(path)) {
//            throw new NoSuchElementException("Channel with id " + id + " not found");
//        }
//        try {
//            Files.delete(path);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Channel findByID(UUID id) {
//        Channel channel = null;
//        Path path = resolvePath(id);
//        if (Files.exists(path)) {
//            try (
//                    FileInputStream fis = new FileInputStream(path.toFile());
//                    ObjectInputStream ois = new ObjectInputStream(fis)
//            ) {
//                channel = (Channel) ois.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        return Optional.ofNullable(channel)
//                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
//    }
//
//    @Override
//    public List<Channel> getAll() {
//        try {
//            return Files.list(DIRECTORY)
//                    .filter(path -> path.toString().endsWith(EXTENSION))
//                    .map(path -> {
//                        try (
//                                FileInputStream fis = new FileInputStream(path.toFile());
//                                ObjectInputStream ois = new ObjectInputStream(fis)
//                        ) {
//                            return (Channel) ois.readObject();
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
//    public Channel updateName(UUID id, String newName) {
//        Channel chNullable = null;
//        Path path = resolvePath(id);
//        if (Files.exists(path)) {
//            try (
//                    FileInputStream fis = new FileInputStream(path.toFile());
//                    ObjectInputStream ois = new ObjectInputStream(fis)
//            ) {
//                chNullable = (Channel) ois.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        Channel channel = Optional.ofNullable(chNullable)
//                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
//        channel.updateName(newName);
//
//        try(
//                FileOutputStream fos = new FileOutputStream(path.toFile());
//                ObjectOutputStream oos = new ObjectOutputStream(fos)
//        ) {
//            oos.writeObject(channel);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return channel;
//    }
//
//
//    // 추가 할 때마다 파일에도 반영 해야 할 것
//    @Override
//    public boolean addMember(UUID channelID, User user) {
//        Channel channel = findByID(channelID);
//        if (channel == null){
//            throw new IllegalStateException("채널에 멤버 추가 실패 (해당 채널이 존재하지 않음) | 채널ID: " + channelID);
//        }
//        return channel.addMember(user.getId());
//    }
//
//    @Override
//    public boolean removeMember(UUID channelID, User user) {
//        Channel channel = findByID(channelID);
//        if (channel == null){
//            throw new IllegalStateException("채널에 멤버 제거 실패 (해당 채널이 존재하지 않음) | 채널ID: " + channelID);
//        }
//        return channel.removeMember(user.getId());
//    }
//
//    @Override
//    public boolean addMessage(UUID channelID, Message message) {
//        Channel channel = findByID(channelID);
//        if (channel == null){
//            throw new IllegalStateException("채널에 메시지 추가 실패 (해당 채널이 존재하지 않음) | 채널ID: " + channelID);
//        }
//        return channel.addMessage(message.getId());
//    }
//
//    @Override
//    public boolean removeMessage(UUID channelID, Message message) {
//        Channel channel = findByID(channelID);
//        if (channel == null){
//            return false;
//        }
//        return channel.removeMessage(message.getId());
//    }
//}
