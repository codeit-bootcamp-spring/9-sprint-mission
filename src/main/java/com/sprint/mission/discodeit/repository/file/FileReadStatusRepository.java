package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileReadStatusRepository
    extends AbstractFileRepository<ReadStatus>
    implements ReadStatusRepository {

    public FileReadStatusRepository(
        @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
        FileLockProvider fileLockProvider) {
        super(fileDirectory, ReadStatus.class, fileLockProvider);
    }

    @Override
    protected UUID getId(ReadStatus entity) {
        return entity.getId();
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return findAll().stream()
            .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
            .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
            .filter(rs -> rs.getUserId().equals(userId))
            .toList();
    }
    @Override
    public void deleteByUserIdAndChannelId(UUID userId, UUID channelId) {
        findAllByUserId(userId).stream()
            .filter(rs -> rs.getChannelId().equals(channelId))
            .forEach(rs -> delete(rs.getId()));
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        findAll().stream()
            .filter(rs -> rs.getChannelId().equals(channelId))
            .forEach(rs -> delete(rs.getId()));
    }
}
