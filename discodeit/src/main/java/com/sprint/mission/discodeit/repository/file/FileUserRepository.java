package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;

public class FileUserRepository implements UserRepository {
    // =========================
    // [추가] 저장 파일 경로
    // =========================
    private static final String FILE_PATH = "data/user.txt";

    public FileUserRepository(String fileDirectory) {
        // =========================
        // [추가] 파일이 없으면 자동 생성
        // =========================
        try {
            Path path = Path.of(FILE_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("유저 파일 생성 실패", e);
        }
    }


// =========================
// [추가] 전체 파일 읽기 → List<User>
// =========================
private List<User> loadAll() {
    List<User> users = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            users.add(new User(
                    UUID.fromString(parts[0]),          // id
                    java.time.Instant.parse(parts[1]).toEpochMilli(),           // createdAt
                    java.time.Instant.parse(parts[2]).toEpochMilli(),           // updatedAt
                    parts[3],                           // loginId
                    parts[4],                           // password
                    parts[5],                           // username
                    parts[6],                           // phoneNumber
                    parts[7]                            // nickname
            ));
        }
    } catch (IOException e) {
        throw new RuntimeException("유저 파일 읽기 실패", e);
    }
    return users;
}

// =========================
// [추가] 전체 덮어쓰기
// =========================
private void saveAll(List<User> users) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
        for (User user : users) {
            writer.write(toLine(user));
            writer.newLine();
        }
    } catch (IOException e) {
        throw new RuntimeException("유저 파일 저장 실패", e);
    }
}
// =========================
// [추가] User → 파일 한 줄 변환
// =========================
private String toLine(User user) {
    return String.join("|",
            user.getId().toString(),
            String.valueOf(user.getCreatedAt()),
            String.valueOf(user.getUpdatedAt()),
            user.getLoginId(),
            user.getPassword(),
            user.getUsername(),
            user.getPhoneNumber(),
            user.getNickname()
    );
}

// =========================
// Repository 구현부
// =========================

@Override
public void create(User user) {
    List<User> users = loadAll();
    users.add(user);
    saveAll(users);
}

@Override
public User findById(UUID id) {
    return loadAll().stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElse(null);
}

@Override
public List<User> findAll() {
    return loadAll();
}

@Override
public boolean update(UUID id, String nickname, String phoneNumber, String password) {
    List<User> users = loadAll();

    for (User user : users) {
        if (user.getId().equals(id)) {
            // ❗ setter 금지, update()만 사용
            user.update(nickname, phoneNumber, password);
            saveAll(users);
            return true;
        }
    }
    return false;
}

@Override
public boolean delete(UUID id) {
    List<User> users = loadAll();
    boolean removed = users.removeIf(u -> u.getId().equals(id));
    if (removed) {
        saveAll(users);
    }
    return removed;
}
    private Instant parseInstant(String raw) {
        if (raw == null || raw.isBlank()) return null;

        // 1) 숫자면 epochMilli로 처리
        if (raw.chars().allMatch(Character::isDigit)) {
            return Instant.ofEpochMilli(Long.parseLong(raw));
        }

        // 2) 아니면 Instant 문자열로 처리 (예: 2026-01-28T15:20:04.043348300Z)
        return Instant.parse(raw);
    }
}