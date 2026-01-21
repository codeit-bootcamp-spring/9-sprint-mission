package service.file;

import entity.Message;
import service.MessageService;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class FileMessageService implements MessageService {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    public FileMessageService() {
        this.DIRECTORY = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                Message.class.getSimpleName()
        );

        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId is null");
        return DIRECTORY.resolve(messageId.toString() + EXTENSION);
    }

    private void writeMessage(Message message) {
        Path path = resolvePath(message.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Message readMessage(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addMessage(Message message) {
        if (message == null || message.getId() == null) return false;

        Path path = resolvePath(message.getId());
        if (Files.exists(path)) return false;

        writeMessage(message);
        return true;
    }

    @Override
    public Message getChannelId(String ChannelId) {
        return getAllMessages().stream()
                .filter(m -> Objects.equals(m.getChannelId(), ChannelId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Message getContent(String Content) {
        return getAllMessages().stream()
                .filter(m -> Objects.equals(m.getContent(), Content))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Message> getUsername(String userName) {
        return getAllMessages().stream()
                .filter(m -> Objects.equals(m.getUsername(), userName))
                .toList();
    }

    @Override
    public List<Message> getAllMessages() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(this::readMessage)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message updateMassage(String oldcontent, String newcontent, String userName, String channelId) {
        Message target = getContent(oldcontent);
        if (target == null) return null;

        if (newcontent != null) target.setContent(newcontent);
        if (userName != null) target.setUserName(userName);
        if (channelId != null) target.setChannelId(channelId);
        target.setUpdatedAt(System.currentTimeMillis());

        writeMessage(target);
        return target;
    }

    @Override
    public boolean deleteMessage(String message) {
        // message 파라미터를 "content"로 보고 삭제 (인터페이스가 id 기반 삭제가 아니라서 이렇게 처리)
        Message target = getContent(message);
        if (target == null) return false;

        Path path = resolvePath(target.getId());
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
