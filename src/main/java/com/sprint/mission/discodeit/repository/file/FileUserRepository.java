package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileUserRepository extends AbstractFileRepository<User> implements UserRepository {

    public FileUserRepository(@Value("${discodeit.repository.file-directory:data}") String fileDirectory,
        FileLockProvider fileLockProvider) {
        super(fileDirectory, User.class, fileLockProvider);
    }

    @Override
    protected UUID getId(User entity) {
        return entity.getId();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream()
            .filter(u -> username.equals(u.getName()))
            .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findAll().stream()
            .filter(u -> email.equals(u.getEmail()))
            .findFirst();
    }

}
