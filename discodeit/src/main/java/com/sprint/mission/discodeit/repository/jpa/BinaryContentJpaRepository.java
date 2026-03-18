package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface BinaryContentJpaRepository extends JpaRepository<BinaryContent, UUID> {
}
