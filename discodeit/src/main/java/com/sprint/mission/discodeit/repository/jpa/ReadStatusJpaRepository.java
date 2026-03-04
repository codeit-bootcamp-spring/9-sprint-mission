package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReadStatusJpaRepository extends JpaRepository<ReadStatus, UUID> {
}
