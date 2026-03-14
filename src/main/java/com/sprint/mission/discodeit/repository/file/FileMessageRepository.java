package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileMessageRepository extends AbstractFileRepository<Message> implements MessageRepository {

    public FileMessageRepository(
        @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
        FileLockProvider fileLockProvider) {
        super(fileDirectory, Message.class, fileLockProvider);
    }

    @Override
    protected UUID getId(Message entity) {
        return entity.getId();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return findAll().stream()
            .filter(m -> channelId.equals(m.getChannelId()))
            .toList();
    }

    @Override
    public List<Message> findBySenderId(UUID senderId) {
        return findAll().stream()
            .filter(m -> senderId.equals(m.getSenderId()))
            .toList();
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        findAllByChannelId(channelId).forEach(m -> super.delete(m.getId()));
    }
}
