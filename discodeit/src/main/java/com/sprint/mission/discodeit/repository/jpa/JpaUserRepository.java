package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository; // 🚩 기존 인터페이스
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jpa")
@Repository
public interface JpaUserRepository extends JpaRepository<User, UUID>, UserRepository {

}
