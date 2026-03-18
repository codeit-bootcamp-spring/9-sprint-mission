package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  Slice<Channel> findByType(ChannelType type, Pageable pageable);

  Slice<Channel> findByTypeOrIdIn(ChannelType type, List<UUID> ids, Pageable pageable);
}
