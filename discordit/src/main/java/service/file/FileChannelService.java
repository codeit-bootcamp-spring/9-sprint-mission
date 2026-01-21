package service.file;

import entity.Channel;
import service.ChannelService;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class FileChannelService implements ChannelService {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    public FileChannelService() {
        this.DIRECTORY = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                Channel.class.getSimpleName()
        );

        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID channelId) {
        return DIRECTORY.resolve(channelId.toString() + EXTENSION);
    }

    private void writeChannel(Channel channel) {
        Path path = resolvePath(channel.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel); // Channel(클래스) 말고 channel(객체)
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Channel readChannel(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // 생성
    @Override
    public boolean addChannel(Channel channel) {
        if (channel == null || channel.getId() == null) return false;

        // name 중복 방지(원하면 제거 가능)
        boolean nameExists = getallChannels().stream()
                .anyMatch(c -> Objects.equals(c.getChannelName(), channel.getChannelName()));
        if (nameExists) return false;

        Path path = resolvePath(channel.getId());
        if (Files.exists(path)) return false;

        writeChannel(channel);
        return true;
    }

    // 조회(UUID)
    @Override
    public Channel getChannelById(UUID id) {
        Path path = resolvePath(id);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("Channel with id " + id + " not found");
        }
        return readChannel(path);
    }

    // 조회(String id) - 인터페이스에 중복으로 있어서 구현
    @Override
    public Channel getChannelById(String channelId) {
        final UUID uuid;
        try {
            uuid = UUID.fromString(channelId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + channelId, e);
        }
        return getChannelById(uuid);
    }

    // 조회(name)
    @Override
    public Channel getChannelByName(String name) {
        return getallChannels().stream()
                .filter(c -> Objects.equals(c.getChannelName(), name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Channel with name " + name + " not found"));
    }

    // 전체 조회
    @Override
    public List<Channel> getallChannels() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(this::readChannel)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ownerId로 목록 조회
    @Override
    public List<Channel> getChannelsByOwnerId(String ownerId) {
        return getallChannels().stream()
                .filter(c -> Objects.equals(c.getownerId(), ownerId))
                .toList();
    }

    // 검색(name 또는 ownerId 포함 검색)
    @Override
    public List<Channel> searchChannels(String nameorownerId) {
        String keyword = (nameorownerId == null) ? "" : nameorownerId;

        return getallChannels().stream()
                .filter(c ->
                        (c.getChannelName() != null && c.getChannelName().contains(keyword)) ||
                                (c.getownerId() != null && c.getownerId().contains(keyword))
                )
                .toList();
    }

    // 수정(oldName으로 찾고 내용 변경 후 저장)
    @Override
    public Channel updateChannel(String oldName, String newName, String description) {
        Channel channel = getChannelByName(oldName);

        // Channel에 update 메서드가 있다면 그걸 쓰는 게 가장 깔끔
        // channel.update(newName, description);

        // 없으면 setter로 처리(예시)
        if (newName != null) channel.setChannelName(newName);
        if (description != null) channel.setDescription(description);

        writeChannel(channel);
        return channel;
    }

    // 삭제(name)
    @Override
    public boolean deleteChannel(String name) {
        Channel channel = getChannelByName(name);
        Path path = resolvePath(channel.getId());

        try {
            // deleteIfExists: 삭제되면 true, 파일이 원래 없으면 false
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 인터페이스에 있는 애매한 메서드: 이름은 "ownerId"인데 파라미터는 channelId(String)
    // 일단 "channelId로 조회" 의미로 구현(= getChannelById와 동일)
    @Override
    public Channel getChannelByownerId(String channelId) {
        return getChannelById(channelId);
    }
}
