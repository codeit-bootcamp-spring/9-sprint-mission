package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.CreateUserStatusDto;
import com.sprint.mission.discodeit.dto.DeleteUserStatusDto;
import com.sprint.mission.discodeit.dto.FindUserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exepction.FailedCreate;
import com.sprint.mission.discodeit.exepction.FailedDelete;
import com.sprint.mission.discodeit.exepction.FailedInit;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("file")
public class FileUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> idMap = new ConcurrentHashMap<>();
    private final Map<UUID, UserStatus> userIdMap = new ConcurrentHashMap<>();
    private final Map<String, UserStatus> userNameMap = new ConcurrentHashMap<>();
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserStatusRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", UserStatus.class.getSimpleName());
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
                            return (UserStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(userStatus -> {
                        idMap.put(userStatus.getId(), userStatus);
                        userIdMap.put(userStatus.getUserId(), userStatus);
                        userNameMap.put(userStatus.getUserName(), userStatus);
                    });
        } catch (IOException e) {
            throw new FailedInit("FileUserStatusRepository init failed");
        }
    }

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public boolean create(CreateUserStatusDto requestDto){
        UserStatus userStatus = new UserStatus(requestDto.id(), requestDto.name());
        idMap.put(userStatus.getId(), userStatus);
        userIdMap.put(requestDto.id(), userStatus);
        userNameMap.put(requestDto.name(), userStatus);

        return save(userStatus);
    }

    @Override
    public String find(FindUserStatusDto requestDto) {
        if(!userIdMap.containsKey(requestDto.id()))
            return "";
        return userIdMap.get(requestDto.id()).isOnline();
    }

    @Override
    public List<String> findAll(List<FindUserStatusDto> requestDto) {
        List<String> result = new ArrayList<>();
        requestDto.forEach(req -> result.add(find(req)));
        return result;
    }

    @Override
    public boolean update(UserStatusUpdateDto requestDto) {
        UserStatus userStatus = userIdMap.get(requestDto.id());

        if(requestDto.time() == null) {
            userStatus.lastAccessTimeUpdater();
        }

        userStatus.lastAccessTimeUpdater(requestDto.time());

        return save(userStatus);
    }

    @Override
    public boolean delete(DeleteUserStatusDto requestDto) {
        UUID id = userIdMap.get(requestDto.id()).getId();

        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new FailedDelete("UserStatus delete failed");
        }

        userIdMap.remove(requestDto.id());
        idMap.remove(userNameMap.remove(requestDto.name()).getId());

        return true;
    }

    private boolean save(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getId());
        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(userStatus);
            return true;
        } catch (IOException e) {
            throw new FailedCreate("UserStatus save failed");
        }
    }
}
