package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelSevice;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelService implements ChannelSevice {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelService() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Channel create(String name, String displayname, UUID admin) {
        Path adminPath = resolvePath(admin);
        if (Files.notExists(adminPath)) {
            throw new IllegalArgumentException("관리자만 채널 생성 가능합니다.");
        }
        Channel channel = new Channel(name, displayname, admin);
        Path Path = resolvePath(channel.getId());

        try (
                FileOutputStream fos = new FileOutputStream(Path.toFile()); // 파일에 바이트를 쓰기 위해 사용
                ObjectOutputStream oos = new ObjectOutputStream(fos); // 직렬화 후 전달
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public Channel find(UUID channelId) {
        Channel channelNullable = null;
        Path path = resolvePath(channelId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                channelNullable = (Channel) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(channelNullable)
                .orElseThrow(() -> new NoSuchElementException("조회 결과: " + channelId));
    }

    @Override
    public List<Channel> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object obj = ois.readObject();
                            if (obj instanceof Channel) {
                                return (Channel) obj;
                            } else {
                                System.out.println("채널이 아닌 객체 무시: " + obj.getClass().getSimpleName());
                                return null;
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                            return null; // 오류 발생 시 null 반환
                        }
                    })
                    .filter(Objects::nonNull) // null 제거
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Channel updateDisplayName(UUID channelId, UUID admin, String newDisplayName) {
        Channel channelNullable = null;
        Path path = resolvePath(admin);

        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                Object obj = ois.readObject();               // Object로 읽기
                if (obj instanceof Channel channel) {        // 타입 체크
                    channel.changeDisplayName(newDisplayName); // 실제 이름 변경
                    channelNullable = channel;

                    // 변경된 채널 저장
                    try (FileOutputStream fos = new FileOutputStream(path.toFile());
                         ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                        oos.writeObject(channel);
                    }

                } else {
                    System.out.println("채널이 아닌 객체 무시: " + obj.getClass().getSimpleName());
                }

            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        return channelNullable;
    }

    @Override
    public void delete(UUID channelId, UUID admin) {
        Path path = resolvePath(channelId);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("채널에 아이디가 " + channelId + " 존재 하지 않습니다.");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
