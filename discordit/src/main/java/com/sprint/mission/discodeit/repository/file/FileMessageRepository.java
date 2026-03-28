package com.sprint.mission.discodeit.repository.file;


import com.sprint.mission.discodeit.dto.response.ResponseMessageDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exepction.FailedCreate;
import com.sprint.mission.discodeit.exepction.FailedDelete;
import com.sprint.mission.discodeit.exepction.FailedInit;
import com.sprint.mission.discodeit.exepction.FailedUpdate;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.MessageRepository;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
public class FileMessageRepository implements MessageRepository {

  private final Map<UUID, List<Message>> channelIdMessageMap = new ConcurrentHashMap<>();
  private final Map<UUID, List<Message>> userIdMessageMap = new ConcurrentHashMap<>();
  private final Map<UUID, Message> messageIdMap = new ConcurrentHashMap<>(128);
  private final FileLockProvider fileLockProvider = new FileLockProvider();
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy년 MM월 dd일 HH시 mm분 ss초").withZone(ZoneId.of("Asia/Seoul"));

  private Path DIRECTORY;
  private final String EXTENSION = ".ser";

  private Path resolvePath(UUID id) {
    String EXTENSION = ".ser";
    return DIRECTORY.resolve(id + EXTENSION);
  }

  @PostConstruct
  public void init() {
    this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map",
        Message.class.getSimpleName());
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
            try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
              return (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }).forEach(message -> {
            channelIdMessageMap
                .computeIfAbsent(message.getSendChannelId(), id -> new ArrayList<>())
                .add(message);

            userIdMessageMap
                .computeIfAbsent(message.getSenderUserId(), id -> new ArrayList<>())
                .add(message);

            messageIdMap.put(message.getId(), message);
          });
    } catch (Exception e) {
      throw new FailedInit("FileMessageRepository init failed");
    }
  }

  @Override
  public ResponseMessageDto create(String content, UUID channelId, UUID userId,
      List<UUID> attachmentIdList) {
    Message message = new Message(channelId, userId, content, attachmentIdList);

    messageIdMap.put(message.getId(), message);
    channelIdMessageMap.computeIfAbsent(channelId, m -> new ArrayList<>()).add(message);
    userIdMessageMap.computeIfAbsent(userId, m -> new ArrayList<>()).add(message);

    Path path = resolvePath(message.getId());
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (
        FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(message);

      return new ResponseMessageDto(
          message.getId(),
          message.getChannelId(),
          message.getUserId(),
          message.getBinaryContentIds(),
          FORMATTER.format(message.getCreateAt()),
          FORMATTER.format(message.getUpdateAt()),
          message.getContent());
    } catch (IOException e) {
      throw new FailedCreate("Message create failed");
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<ResponseMessageDto> findAllInChannel(UUID channelId) {
    List<ResponseMessageDto> result = new ArrayList<>();
    try {
      List<Message> messages = channelIdMessageMap.get(channelId);
      messages.stream().sorted(Comparator.comparing(Message::getCreateAt))
          .forEach(message -> {
            result.add(new ResponseMessageDto(
                message.getId(),
                message.getChannelId(),
                message.getUserId(),
                message.getBinaryContentIds(),
                FORMATTER.format(message.getCreateAt()),
                FORMATTER.format(message.getUpdateAt()), message.getContent()
            ));
          });
      return result;
    } catch (Exception e) {
      return List.of();
    }
  }

  @Override
  public Instant getLastMessageInChannel(UUID channelId) {
    try {
      return channelIdMessageMap.get(channelId)
          .stream().max(Comparator.comparing(Message::getCreateAt)).orElse(null).getCreateAt();
    } catch (NullPointerException e) {
      throw new NotFound("Last message not found");
    }
  }

  @Override
  public List<ResponseMessageDto> findAllForSender(UUID userId) {
    List<ResponseMessageDto> result = new ArrayList<>();
    List<Message> messages = userIdMessageMap.get(userId);
    try {
      if (messages != null) {
        messages.stream().sorted(Comparator.comparing(Message::getCreateAt))
            .forEach(message -> {
              result.add(new ResponseMessageDto(
                  message.getId(),
                  message.getChannelId(),
                  message.getUserId(),
                  message.getBinaryContentIds(),
                  FORMATTER.format(message.getCreateAt()),
                  FORMATTER.format(message.getUpdateAt()),
                  message.getContent()
              ));
            });
      }
    } catch (Exception e) {
      throw new NotFound("Message not found");
    }
    return result;
  }

  @Override
  public ResponseMessageDto updateMessage(UUID id, String content) {
    Message message = messageIdMap.get(id);
    if (message == null) {
      throw new NotFound("Message not found");
    }

    message.updateMessage(content);

    Path path = resolvePath(id);
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      oos.writeObject(message);
      return new ResponseMessageDto(
          message.getId(),
          message.getChannelId(),
          message.getUserId(),
          message.getBinaryContentIds(),
          FORMATTER.format(message.getCreateAt()),
          FORMATTER.format(message.getUpdateAt()),
          message.getContent());
    } catch (IOException e) {
      throw new FailedUpdate("Message update failed");
    } finally {
      lock.unlock();
    }
  }

  @Override
  public UUID delete(UUID userId, UUID id) {
    List<Message> userMessages = userIdMessageMap.get(userId);
    if (userMessages == null) {
      throw new NotFound("Message not found(Delete)");
    }

    Message message = userMessages.stream().filter(e -> e.getId().equals(id)).findFirst()
        .orElse(null);
    if (message == null) {
      throw new NotFound("Message not found(Delete)");
    }

    UUID channelId = message.getSendChannelId();

    Path path = resolvePath(id);
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try {
      Files.deleteIfExists(path);
      userIdMessageMap.get(userId).remove(message);

      if (channelIdMessageMap.containsKey(channelId)) {
        channelIdMessageMap.get(channelId).remove(message);
      }
      messageIdMap.remove(message.getId());
      return message.getId();
    } catch (IOException e) {
      throw new FailedDelete("Message delete failed");
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<List<UUID>> deleteAll(UUID id) {
    List<List<UUID>> result = new ArrayList<>();
    if (channelIdMessageMap.containsKey(id)) {
      new ArrayList<>(channelIdMessageMap.get(id)).forEach(message -> {
        result.add(message.getBinaryContentIds());
        delete(message.getUserId(), message.getId());
      });
    }
    if (userIdMessageMap.containsKey(id)) {
      new ArrayList<>(userIdMessageMap.get(id)).forEach(message -> {
        result.add(message.getBinaryContentIds());
        delete(message.getUserId(), message.getId());
      });
    }
    return result;
  }

  @Override
  public boolean isPresentMessage(UUID userId, UUID id) {
    List<Message> messages = userIdMessageMap.get(userId);
    if (messages == null) {
      return false;
    }

    Object result = messages.stream().filter(message -> message.getId().equals(id)).findFirst()
        .orElse(null);
    return result != null;
  }
}
