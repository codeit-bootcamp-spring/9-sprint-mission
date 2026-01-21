package service.file;

import entity.Message;
import entity.User;
import service.ChannelService;
import service.MessageService;
import service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;


public class FileMessageService implements MessageService,Serializable {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(String name) {
        return DIRECTORY.resolve(name + EXTENSION);
    }

    @Override
    public void sendMessage(Message message) {
        String fileName = message.getReceiver().getUsername();
        Path path = resolvePath(fileName);
        List<Message> history;
        if (Files.exists(path)) {
            try (
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                history = (List<Message>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            history = new ArrayList<>();
        }
        history.add(message);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()));
            oos.writeObject(history);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Message> getMessages() {
        List<Message> allMessages = new ArrayList<>();

        // 1. [조사] 폴더 내의 모든 파일 경로를 가져옵니다.
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            // 2. 확장자가 .ser인 파일들만 골라냅니다.
            List<Path> filePaths = stream
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .toList();

            // 3. [반복] 각 파일 주소로 가서 데이터를 빨아들입니다(Input).
            for (Path path : filePaths) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    // 팩트: 각 파일은 List<Message>를 담고 있으므로 통째로 꺼냅니다.
                    List<Message> history = (List<Message>) ois.readObject();
                    // 전체 바구니에 합칩니다.
                    allMessages.addAll(history);
                } catch (Exception e) {
                    // 한 파일이 깨졌다고 전체 조회를 멈출 순 없으니 무시하고 다음 파일로!
                    System.err.println(path.getFileName() + " 읽기 실패: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장소 접근 불가", e);
        }

        return allMessages; // 모든 파일에서 긁어모은 최종 결과물
    }

    @Override
    public List<Message> getSenderMessages(User sender) {
        return getMessages().stream()
                .filter(m -> m.getSender().equals(sender))
                .toList();


    }

    @Override
    public List<Message> getReceiverMessages(User receiver) {
        return getMessages().stream()
                .filter(m -> m.getReceiver().equals(receiver))
                .toList();
    }

    @Override
    public boolean deleteMessage(String message, User receiver) {
        Path path = resolvePath(message);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("no message");

        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    return true;
}



}