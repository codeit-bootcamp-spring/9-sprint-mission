package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exepction.FailedDelete;
import com.sprint.mission.discodeit.exepction.FailedInit;
import com.sprint.mission.discodeit.exepction.FailedUpdate;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("file")
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> idReadStatusMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<ReadStatus>> userIdReadStatusMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<ReadStatus>> channelIdReadStatusMap = new ConcurrentHashMap<>();
    private Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @PostConstruct
    public void init() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", ReadStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.list(DIRECTORY).filter(path-> path.toString().endsWith(EXTENSION))
                    .map(path->{
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(readstatus -> {
                        idReadStatusMap.put(readstatus.getId(), readstatus);
                        userIdReadStatusMap.computeIfAbsent(readstatus.getUserId(), id -> new ArrayList<>()).add(readstatus);
                    });
        } catch (Exception e) {
            throw new FailedInit("FileReadStatusRepository init failed");
        }
    }

    @Override
    public void create(UUID userId, UUID channelId) {
        ReadStatus readStatus = new ReadStatus(userId, channelId);
        idReadStatusMap.put(readStatus.getId(), readStatus);
        userIdReadStatusMap.computeIfAbsent(userId, id -> new ArrayList<>()).add(readStatus);
        channelIdReadStatusMap.computeIfAbsent(userId, id -> new ArrayList<>()).add(readStatus);
        Path path = resolvePath(readStatus.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new FailedInit("FileReadStatusRepository init failed");
        }
    }

    @Override
    public Instant find(UUID id) {
        return idReadStatusMap.get(id).getUpdateAt();
    }

    @Override
    public Map<UUID, Instant> findAllByUserId(UUID userId) {
        List<ReadStatus> tempList = userIdReadStatusMap.get(userId);
        Map<UUID, Instant> result = new HashMap<>();
        for(ReadStatus temp : tempList) {
            result.put(temp.getChannelId(), temp.getUpdateAt());
        }

        return result;
    }

    @Override
    public boolean update(UUID userId, UUID channelId) {
        ReadStatus temp = userIdReadStatusMap.get(userId).stream().filter(readStatus -> readStatus.getChannelId().equals(channelId)).findFirst().orElse(null);

        if(temp == null) throw new NotFound("상태값을 찾지 못했습니다.");

        Path path = resolvePath(temp.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(temp);
            temp.updateReadAt();
            return true;
        } catch (IOException e) {
            throw new FailedUpdate("ReadStatus update failed");
        }
    }

    @Override
    public boolean delete(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
            ReadStatus temp = idReadStatusMap.remove(id);
            userIdReadStatusMap.remove(temp.getUserId());
            channelIdReadStatusMap.remove(temp.getChannelId());
            return true;
        } catch (IOException e) {
            throw new FailedDelete("ReadStatus delete failed");
        }
    }

    @Override
    public void deleteForChannel(UUID channelId) {
        List<ReadStatus> temp = channelIdReadStatusMap.get(channelId);
        temp.forEach(readStatus -> delete(readStatus.getId()));
    }

    @Override
    public void deleteForUser(UUID userId) {
        List<ReadStatus> temp = userIdReadStatusMap.get(userId);
        temp.forEach(readStatus -> delete(readStatus.getId()));
    }
}
