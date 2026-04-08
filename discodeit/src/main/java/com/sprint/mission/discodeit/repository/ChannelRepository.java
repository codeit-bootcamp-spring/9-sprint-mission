package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  @Query("SELECT c FROM Channel c WHERE c.type = 'PUBLIC' OR c.id IN (SELECT r.channel.id FROM ReadStatus r WHERE r.user.id = :userId)")
  Slice<Channel> findAllByUserId(UUID userId, Pageable pageable);

}
