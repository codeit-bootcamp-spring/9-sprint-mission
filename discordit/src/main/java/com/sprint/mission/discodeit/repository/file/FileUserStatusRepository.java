package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestDeleteUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestFindUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exepction.FailedCreate;
import com.sprint.mission.discodeit.exepction.FailedDelete;
import com.sprint.mission.discodeit.exepction.FailedInit;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class FileUserStatusRepository implements UserStatusRepository {

  private final Map<UUID, UserStatus> idMap = new ConcurrentHashMap<>();
  private final Map<UUID, UserStatus> userIdMap = new ConcurrentHashMap<>();
  private final Map<String, UserStatus> usernameMap = new ConcurrentHashMap<>();
  private final FileLockProvider fileLockProvider = new FileLockProvider();
  private final Path DIRECTORY;
  private final String EXTENSION = ".ser";

  public FileUserStatusRepository() {
    this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map",
        UserStatus.class.getSimpleName());
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
            usernameMap.put(userStatus.getUsername(), userStatus);
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
  public boolean create(RequestCreateUserStatusDto requestDto) {
    UserStatus userStatus = new UserStatus(requestDto.id(), requestDto.name());
    idMap.put(userStatus.getId(), userStatus);
    userIdMap.put(requestDto.id(), userStatus);
    usernameMap.put(requestDto.name(), userStatus);

    return save(userStatus);
  }

  @Override
  public Boolean find(RequestFindUserStatusDto requestDto) {
    if (!userIdMap.containsKey(requestDto.id())) {
      return null;
    }
    return userIdMap.get(requestDto.id()).isOnline();
  }

  @Override
  public List<Boolean> findAll(List<RequestFindUserStatusDto> requestDto) {
    List<Boolean> result = new ArrayList<>();
    requestDto.forEach(req -> result.add(find(req)));
    return result;
  }

  @Override
  public UserStatus update(RequestUpdateUserStatusDto requestDto) {
    UserStatus userStatus = userIdMap.get(requestDto.id());

    if (requestDto.time() == null) {
      userStatus.lastAccessTimeUpdater();
    }

    userStatus.lastAccessTimeUpdater(requestDto.time());

    save(userStatus);

    return userStatus;
  }

  @Override
  public boolean delete(RequestDeleteUserStatusDto requestDto) {
    UUID id = userIdMap.get(requestDto.id()).getId();

    Path path = resolvePath(id);
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new FailedDelete("UserStatus delete failed");
    } finally {
      lock.unlock();
    }

    userIdMap.remove(requestDto.id());
    idMap.remove(usernameMap.remove(requestDto.name()).getId());

    return true;
  }

  private boolean save(UserStatus userStatus) {
    Path path = resolvePath(userStatus.getId());
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      oos.writeObject(userStatus);
      return true;
    } catch (IOException e) {
      throw new FailedCreate("UserStatus save failed");
    } finally {
      lock.unlock();
    }
  }
}
