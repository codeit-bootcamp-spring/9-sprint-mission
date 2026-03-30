package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileUserStatusRepository extends AbstractFileRepository<UserStatus> implements UserStatusRepository {

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory:data}") String fileDirectory,
        FileLockProvider fileLockProvider) {
        super(fileDirectory, UserStatus.class, fileLockProvider);
    }

    @Override
    protected UUID getId(UserStatus entity) {
        return entity.getId();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
            .filter(u -> userId.equals(u.getUserId()))
            .findFirst();
    }
}
