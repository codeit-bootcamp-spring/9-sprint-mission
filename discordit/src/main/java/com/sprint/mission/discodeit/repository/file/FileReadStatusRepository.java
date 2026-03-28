package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.request.RequestCreateReadStatusDto;
import com.sprint.mission.discodeit.dto.response.ResponseReadStatus;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exepction.FailedDelete;
import com.sprint.mission.discodeit.exepction.FailedInit;
import com.sprint.mission.discodeit.exepction.FailedUpdate;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    prefix = "discodeit.repository",
    name = "type",
    havingValue = "file",
    matchIfMissing = false
)
public class FileReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatus> idReadStatusMap = new ConcurrentHashMap<>();
  private final Map<UUID, List<ReadStatus>> userIdReadStatusMap = new ConcurrentHashMap<>();
  private final Map<UUID, List<ReadStatus>> channelIdReadStatusMap = new ConcurrentHashMap<>();
  private final FileLockProvider fileLockProvider = new FileLockProvider();
  private Path DIRECTORY;
  private final String EXTENSION = ".ser";

  private Path resolvePath(UUID id) {
    String EXTENSION = ".ser";
    return DIRECTORY.resolve(id + EXTENSION);
  }

  @PostConstruct
  public void init() {
    this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map",
        ReadStatus.class.getSimpleName());
    if (Files.notExists(DIRECTORY)) {
      try {
        Files.createDirectories(DIRECTORY);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    try {
      Files.list(DIRECTORY).filter(path -> path.toString().endsWith(EXTENSION))
          .map(path -> {
            try (FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)) {
              return (ReadStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }).forEach(readstatus -> {
            idReadStatusMap.put(readstatus.getId(), readstatus);
            userIdReadStatusMap.computeIfAbsent(readstatus.getUserId(), id -> new ArrayList<>())
                .add(readstatus);
          });
    } catch (Exception e) {
      throw new FailedInit("FileReadStatusRepository init failed");
    }
  }

  @Override
  public List<ResponseReadStatus> create(RequestCreateReadStatusDto request) {
    List<ResponseReadStatus> result = new ArrayList<>();
    for (UUID channelId : request.channelIds()) {
      result.add(this.create(request.userId(), channelId));
    }
    return result;
  }

  private ResponseReadStatus create(UUID userId, UUID channelId) {
    ReadStatus readStatus = new ReadStatus(userId, channelId);
    idReadStatusMap.put(readStatus.getId(), readStatus);
    userIdReadStatusMap.computeIfAbsent(userId, id -> new ArrayList<>()).add(readStatus);
    channelIdReadStatusMap.computeIfAbsent(userId, id -> new ArrayList<>()).add(readStatus);
    Path path = resolvePath(readStatus.getId());
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      oos.writeObject(readStatus);
    } catch (IOException e) {
      throw new FailedInit("FileReadStatusRepository init failed");
    } finally {
      lock.unlock();
    }
    return response(readStatus);
  }

  private ResponseReadStatus response(ReadStatus readStatus) {
    return new ResponseReadStatus(readStatus.getId(), readStatus.getUserId(),
        readStatus.getChannelId(), readStatus.getCreateAt(), readStatus.getUpdateAt(),
        readStatus.getLastReadAt());
  }

  @Override
  public Instant find(UUID id) {
    return idReadStatusMap.get(id).getUpdateAt();
  }

  @Override
  public List<ResponseReadStatus> findAllByUserId(UUID userId) {
    List<ResponseReadStatus> result = new ArrayList<>();

    for (ReadStatus readStatus : userIdReadStatusMap.get(userId)) {
      result.add(response(readStatus));
    }
    return result;
  }

  @Override
  public ResponseReadStatus update(UUID userId, UUID channelId) {
    ReadStatus temp = userIdReadStatusMap.get(userId).stream()
        .filter(readStatus -> readStatus.getChannelId().equals(channelId)).findFirst().orElse(null);

    if (temp == null) {
      throw new NotFound("상태값을 찾지 못했습니다.");
    }

    Path path = resolvePath(temp.getId());
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      oos.writeObject(temp);
      temp.updateReadAt();
      return response(temp);
    } catch (IOException e) {
      throw new FailedUpdate("ReadStatus update failed");
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean delete(UUID id) {
    Path path = resolvePath(id);
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try {
      Files.delete(path);
      ReadStatus temp = idReadStatusMap.remove(id);
      userIdReadStatusMap.remove(temp.getUserId());
      channelIdReadStatusMap.remove(temp.getChannelId());
      return true;
    } catch (IOException e) {
      throw new FailedDelete("ReadStatus delete failed");
    } finally {
      lock.unlock();
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
