import com.sprint.mission.discodeit.dto.user.UserView;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.exception.NotFoundException;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// (원하면 JCF도 스위칭 가능)
// import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
// import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
// import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JavaApplication {

        // 선택) 파일 데이터 누적 방지: 데모용 초기화
    private static void clearDirectory(Path dir, String extension) {
        if (Files.notExists(dir)) return;

        try (var stream = Files.list(dir)) {
            stream
                    .filter(path -> path.getFileName().toString().endsWith(extension))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete file: " + path, e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to clear directory: " + dir, e);
        }
    }

    public static void main(String[] args) {

        // 파일초기화
        Path base = Paths.get(System.getProperty("user.dir"), "file-data-map");
        clearDirectory(base.resolve("User"), ".ser");
        clearDirectory(base.resolve("Channel"), ".ser");
        clearDirectory(base.resolve("Message"), ".ser");

        //Repository 초기화 (File or JCF 스위칭 가능)
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        // 임시 In-Memory Repository (과제 요구: File/JCF 구현체는 아직 만들지 않는 단계)
        UserStatusRepository userStatusRepository = new UserStatusRepository() {
            private final Map<UUID, UserStatus> store = new ConcurrentHashMap<>();

            @Override
            public UserStatus save(UserStatus userStatus) {
                store.put(userStatus.getId(), userStatus);
                return userStatus;
            }

            @Override
            public Optional<UserStatus> findById(UUID id) {
                return Optional.ofNullable(store.get(id));
            }

            @Override
            public Optional<UserStatus> findByUserId(UUID userId) {
                return store.values().stream()
                        .filter(s -> userId.equals(s.getUserId()))
                        .findFirst();
            }

            @Override
            public List<UserStatus> findAll() {
                return new ArrayList<>(store.values());
            }

            @Override
            public void deleteById(UUID id) {
                store.remove(id);
            }

            @Override
            public boolean existsById(UUID id) {
                return store.containsKey(id);
            }
        };

        BinaryContentRepository binaryContentRepository = new BinaryContentRepository() {
            private final Map<UUID, BinaryContent> store = new ConcurrentHashMap<>();

            @Override
            public BinaryContent save(BinaryContent binaryContent) {
                store.put(binaryContent.getId(), binaryContent);
                return binaryContent;
            }

            @Override
            public Optional<BinaryContent> findById(UUID id) {
                return Optional.ofNullable(store.get(id));
            }

            @Override
            public List<BinaryContent> findAll() {
                return new ArrayList<>(store.values());
            }

            @Override
            public void deleteById(UUID id) {
                store.remove(id);
            }

            @Override
            public boolean existsById(UUID id) {
                return store.containsKey(id);
            }
        };

        // JCF 초기화
        // UserRepository userRepository = new JCFUserRepository();
        // ChannelRepository channelRepository = new JCFChannelRepository();
        // MessageRepository messageRepository = new JCFMessageRepository();

        //Service 초기화 (Basic*Service + DI)
        UserService userService = new BasicUserService(userRepository, userStatusRepository, binaryContentRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService =
                new BasicMessageService(messageRepository, userRepository, channelRepository);


        printlnTitle("\n=== 1. CREATE (유저/채널/메시지 생성) ===");

        User user = userService.create("Seongjun", "seongjun@test.com", "010-1234-5678");
        printUser(user);

        Channel channel = channelService.create(ChannelType.PUBLIC, "공용방", "generate", user.getId());
        printChannel(channel);

        Message message = messageService.create(channel.getId(), user.getId(), "안녕하세요!");
        printMessage(message);


        //DI 검증 실패
        printlnTitle("\n=== 1-1. DI 검증 실패 케이스 (관계 검증/입력 검증) ===");

        expectNotFound("실패 1: 채널 없이 생성",
                () -> messageService.create(UUID.randomUUID(), user.getId(), "채널 없음"));

        expectNotFound("실패 2: 유저 없이 생성",
                () -> messageService.create(channel.getId(), UUID.randomUUID(), "유저 없음"));

        expectIllegal("실패 3: 내용 없이 생성",
                () -> messageService.create(channel.getId(), user.getId(), ""));


        printlnTitle("\n=== 2. READ (단건 조회) ===");

        UserView foundUser = userService.findById(user.getId());
        System.out.println("유저 찾기: " + safe(foundUser, User::getDisplayName));

        Channel foundChannel = channelService.findById(channel.getId());
        System.out.println("채널 찾기: " + safe(foundChannel, Channel::getName));

        Message foundMessage = messageService.findById(message.getId());
        System.out.println("메세지 찾기: " + safe(foundMessage, Message::getContent));

        System.out.println("\n=== 3. READ ALL (전체 조회) ===");

        System.out.println("유저 수: " + userService.findAll().size());
        System.out.println("채널 수: " + channelService.findAll().size());
        System.out.println("메시지 수: " + messageService.findAll().size());
        System.out.println(messageService.findAll().stream());

        printResultHighlights(
                "유저 이름들",
                userService.findAll().stream().map(User::getDisplayName).toList(),
                5
        );

        printResultHighlights(
                "채널 이름들",
                channelService.findAll().stream().map(Channel::getName).toList(),
                5
        );

        printResultHighlights(
                "메시지들",
                messageService.findAll().stream().map(m -> "\"" + m.getContent() + "\"").toList(),
                5
        );


        printlnTitle("\n=== 4. 등록 여부 (boolean) ===");
        System.out.println("User 등록: " + userService.existsById(user.getId()));
        System.out.println("User email 등록: " + userService.existsByEmail(user.getEmail()));
        System.out.println("User phone 등록: " + userService.existsByPhoneNumber(user.getPhoneNumber()));
        System.out.println("Channel 등록: " + channelService.existsById(channel.getId()));
        System.out.println("Message 등록: " + messageService.existsById(message.getId()));


        printlnTitle("\n=== 5. UPDATE (수정) ===");

        userService.update(user.getId(), "Seongjun Yun", "seongjunyun@test.com", "010-0000-0000");
        System.out.println("수정된 User Name: " + userService.findById(user.getId()).getDisplayName());

        channelService.update(channel.getId(), "ian", "notice");
        System.out.println("수정된 Channel Name: " + channelService.findById(channel.getId()).getName());

        messageService.update(message.getId(), "수정된 메세지 내용~");
        System.out.println("수정된 Message Content: " + messageService.findById(message.getId()).getContent());


        printlnTitle("\n=== 6. DELETE (삭제) ===");

        messageService.delete(message.getId());
        channelService.delete(channel.getId());
        userService.delete(user.getId());

        System.out.println("삭제 후 Users size: " + userService.findAll().size());
        System.out.println("삭제 후 Channels size: " + channelService.findAll().size());
        System.out.println("삭제 후 Messages size: " + messageService.findAll().size());


        printlnTitle("\n=== 7. EXISTS after delete (삭제 후 등록 여부) ===");
        System.out.println("User 등록 여부? " + userService.existsById(user.getId()));
        System.out.println("Channel 등록 여부? " + channelService.existsById(channel.getId()));
        System.out.println("Message 등록 여부? " + messageService.existsById(message.getId()));

        System.out.println("\n=== DONE ===");
    }

    // ===== 출력 유틸 =====
    private static void printlnTitle(String title) {
        System.out.println(title);
    }

    private static void printUser(User u) {
        System.out.println("유저 생성: " + u.getId() + " / " + u.getDisplayName()
                + " / " + u.getEmail() + " / " + u.getPhoneNumber());
    }

    private static void printChannel(Channel c) {
        System.out.println("채널 생성: " + c.getId() + " / " + c.getName()
                + " (ownerId=" + c.getOwnerId() + ")");
    }

    private static void printMessage(Message m) {
        System.out.println("메세지 생성: " + m.getId() + " / " + m.getContent()
                + " (channelId=" + m.getChannelId() + ", senderId=" + m.getSenderId() + ")");
    }

    private static void printResultHighlights(String label, List<String> results, int limit) {
        int totalCount = results.size();
        if (totalCount == 0) {
            System.out.println(label + ": 0개");
            return;
        }

        String highlights = results.stream()
                .limit(limit)
                .collect(Collectors.joining(", "));

        String moreSuffix = totalCount > limit
                ? " ... (+" + (totalCount - limit) + " more)"
                : "";

        System.out.println(label + ": " + totalCount + "개 | " + highlights + moreSuffix);
    }

    private static <T> String safe(T obj, java.util.function.Function<T, String> mapper) {
        return Optional.ofNullable(obj).map(mapper).orElse("null");
    }

    // ===== 예외 테스트 =====
    private static void expectNotFound(String label, Runnable action) {
        System.out.println("\n- " + label);
        try {
            action.run();
            System.out.println("expected NotFoundException, but succeeded");
        } catch (NotFoundException e) {
            System.out.println("expected error: " + e.getMessage());
        }
    }

    private static void expectIllegal(String label, Runnable action) {
        System.out.println("\n- " + label);
        try {
            action.run();
            System.out.println("expected IllegalArgumentException, but succeeded");
        } catch (IllegalArgumentException e) {
            System.out.println("expected error: " + e.getMessage());
        }
    }
}