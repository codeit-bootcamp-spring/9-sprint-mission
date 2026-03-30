package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileChannelRepository extends AbstractFileRepository<Channel> implements ChannelRepository {

    public FileChannelRepository(@Value("${discodeit.repository.file-directory:data}") String fileDirectory,
        FileLockProvider fileLockProvider) {
        super(fileDirectory, Channel.class, fileLockProvider);
    }

    @Override
    protected UUID getId(Channel entity) {
        return entity.getId();
    }

    @Override
    public List<Channel> findAllPublic() {
        return List.of();
    }

    @Override
    public List<Channel> findPrivateChannelsByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return findAll().stream()
            .filter(c -> name.equals(c.getName()))
            .findFirst();
    }
}
