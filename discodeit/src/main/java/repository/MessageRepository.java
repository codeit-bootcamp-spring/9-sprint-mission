package repository;

import entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID messageId);
    List<Message> findAll();
    List<Message> findAllByChannelId(UUID channelId);
    void deleteById(UUID messageId);
    boolean existsById(UUID messageId);
}
