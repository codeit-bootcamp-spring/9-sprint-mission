package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.dto.UpdateUserDto;
import com.sprint.mission.discodeit.dto.UserFinder;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exepction.*;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("file")
public class FileUserRepository implements UserRepository {
    private final Map<UUID, User> idUserMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> userNameIdMap = new ConcurrentHashMap<>();
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(user -> {
                        idUserMap.put(user.getId(), user);
                        userNameIdMap.put(user.getName(), user.getId());
                    });
        } catch (IOException e) {
            throw new FailedInit("FileUserRepository init failed");
        }
    }

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public UUID create(CreateUserDto dto) {
        User user = dto.toEntity();
        userNameIdMap.put(user.getName(), user.getId());
        idUserMap.put(user.getId(), user);
        Path path = resolvePath(user.getId());

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
            return user.getId();
        } catch (IOException e) {
            throw new FailedCreate("FileBinaryContentRepository create failed");
        }
    }

    @Override
    public boolean update(UpdateUserDto requestDto) {
        UUID userId = requestDto.id();
        String reName = requestDto.reName();
        String rePassword = requestDto.rePassword();
        String reMail = requestDto.reMail();
        String rePhoneNumber = requestDto.rePhoneNumber();
        UUID reProfileId = requestDto.reProfileId();

        Path path = resolvePath(userId);

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(idUserMap.get(userId));
            idUserMap.get(userId).updateUser(reName, rePassword, reMail, rePhoneNumber, reProfileId);
            return true;
        } catch (IOException e) {
            throw new FailedUpdate("User update failed");
        }
    }

    @Override
    public UserFinder find(String name) {
        User user = idUserMap.get(userNameIdMap.get(name));
        UUID id = user.getId();
        String userName = user.getName();

        return new UserFinder(id, userName, user.toString(), user.getProfileId());
    }

    @Override
    public List<UserFinder> findAll() {
        List<UserFinder> result = new ArrayList<>();
        idUserMap.values().stream().sorted(Comparator.comparing(User::getName)).forEach(user -> {
            UUID id = user.getId();
            String userName = user.getName();
            result.add(new UserFinder(id, userName, user.toString(), user.getProfileId()));
        });
        return result;
    }

    @Override
    public boolean delete(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new FailedDelete("User delete failed");
        }
        userNameIdMap.remove(idUserMap.get(id).getName());
        idUserMap.remove(id);
        return true;
    }

    @Override
    public UUID userNameToId(String name) {
        try {
            return userNameIdMap.get(name);
        } catch (Exception e) {
            throw new FailedFound("Do not found this user : " + name);
        }
    }

    @Override
    public String userIdToName(UUID id) {
        try {
            return idUserMap.get(id).getName();
        } catch (Exception e) {
            throw new FailedFound("Do not found this user : " + id);
        }
    }

    @Override
    public boolean checkInvalid(UUID id, String pw) {
        try {
            return !idUserMap.get(id).getPassword().equals(pw);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public boolean duplicateChecker(String checkThis, String findThis) {
        try {
            if (checkThis.equals("이메일") && idUserMap.values().stream().anyMatch(u -> u.getEmail().equals(findThis))) return true;
            if (checkThis.equals("전화번호") && idUserMap.values().stream().anyMatch(u -> u.getPhoneNumber().equals(findThis))) return true;
            if (checkThis.equals("사용자명") && userNameIdMap.get(findThis) != null) return true;
        } catch (Exception ignored) {}
        return false;
    }
}
