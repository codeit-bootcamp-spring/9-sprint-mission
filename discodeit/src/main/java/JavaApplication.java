import service.ChannelService;
import service.MessageService;
import service.UserService;

import service.file.FileChannelService;
import service.file.FileMessageService;
import service.file.FileUserService;

import entity.Channel;
import entity.Message;
import entity.User;

import exception.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JavaApplication {

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

        Path base = Paths.get(System.getProperty("user.dir"), "file-data-map");

        clearDirectory(base.resolve("User"), ".ser");
        clearDirectory(base.resolve("Channel"), ".ser");
        clearDirectory(base.resolve("Message"), ".ser");


        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService(userService, channelService); // DI

        printlnTitle("\n=== 1. CREATE (유저/채널/메시지 생성) ===");

        User user = userService.create("Seongjun", "seongjun@test.com", "010-1234-5678");
        printUser(user);

        Channel channel = channelService.create("general", user.getId());
        printChannel(channel);

        Message message = messageService.create(channel.getId(), user.getId(), "안녕하세요!");
        printMessage(message);

        printlnTitle("\n=== 1-1. DI 검증 실패 케이스 (관계 검증/입력 검증) ===");

        // 실패 1: 없는 채널
        expectNotFound("실패 1: 채널 없이 생성",
                () -> messageService.create(UUID.randomUUID(), user.getId(), "채널 없음"));

        // 실패 2: 없는 유저
        expectNotFound("실패 2: 유저 없이 생성",
                () -> messageService.create(channel.getId(), UUID.randomUUID(), "유저 없음"));

        // 실패 3: 빈 content
        expectIllegal("실패 3: 내용 없이 생성",
                () -> messageService.create(channel.getId(), user.getId(), ""));

        printlnTitle("\n=== 2. READ (단건 조회) ===");

        User foundUser = userService.findById(user.getId());
        System.out.println("유저 찾기: " + safe(foundUser, User::getDisplayName));

        Channel foundChannel = channelService.findById(channel.getId());
        System.out.println("채널 찾기: " + safe(foundChannel, Channel::getName));

        Message foundMessage = messageService.findById(message.getId());
        System.out.println("메세지 찾기: " + safe(foundMessage, Message::getContent));

        System.out.println("\n=== 3. READ ALL (전체 조회) ===");

// size 요약
        System.out.println("유저 수: " + userService.findAll().size());
        System.out.println("채널 수: " + channelService.findAll().size());
        System.out.println("메시지 수: " + messageService.findAll().size());

// preview 출력 (앞 5개만)
        printResultHighlights(
                "유저 이름들",
                userService.findAll().stream()
                        .map(User::getDisplayName)
                        .toList(), 5
        );

        printResultHighlights(
                "채널 이름들",
                channelService.findAll().stream()
                        .map(Channel::getName)
                        .toList(), 5
        );

        printResultHighlights(
                "메시지들",
                messageService.findAll().stream()
                        .map(m -> "\"" + m.getContent() + "\"")
                        .toList(), 5
        );

        printlnTitle("\n=== 4. 등록 여부 (boolean) ===");

        System.out.println("User 등록: " + userService.existsById(user.getId()));

        System.out.println("Channel 등록: " + channelService.existsById(channel.getId()));

        System.out.println("Message 등록: " + messageService.existsById(message.getId()));

        printlnTitle("\n=== 5. UPDATE (수정) ===");

        userService.update(user.getId(), "Seongjun Yun", "seongjunyun@test.com", "010-0000-0000");
        System.out.println("수정된 User Name: " + userService.findById(user.getId()).getDisplayName());

        channelService.update(channel.getId(), "notice");
        System.out.println("수정된 Channel Name: " + channelService.findById(channel.getId()).getName());

        messageService.update(message.getId(), "수정된 메세지 내용~");
        System.out.println("수정된 Message Content: " + messageService.findById(message.getId()).getContent());

        printlnTitle("\n=== 6. DELETE (삭제) ===");

        // 의존성 역순(메시지 → 채널 → 유저)
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

    // ===== 출력 유틸 (static) =====

    private static void printlnTitle(String title) {
        System.out.println(title);
    }

    private static void printUser(User u) {
        System.out.println("유저 생성" + ": " + u.getId() + " / " + u.getDisplayName()
                + " / " + u.getEmail() + " / " + u.getPhoneNumber());
    }

    private static void printChannel(Channel c) {
        System.out.println("채널 생성" + ": " + c.getId() + " / " + c.getName()
                + " (ownerId=" + c.getOwnerId() + ")");
    }

    private static void printMessage(Message m) {
        System.out.println("메세지 생성" + ": " + m.getId() + " / " + m.getContent()
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

        System.out.println(
                label + ": " + totalCount + "개 | " + highlights + moreSuffix
        );
    }

    // null-safe 출력
    private static <T> String safe(T obj, java.util.function.Function<T, String> mapper) {
        return Optional.ofNullable(obj).map(mapper).orElse("null");
    }

    // ===== 예외 테스트 유틸 =====

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