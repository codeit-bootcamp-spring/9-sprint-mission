package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  @Query("SELECT DISTINCT c FROM Channel c "
      + "LEFT JOIN FETCH c.messages m "
      + "WHERE c.id = :channelId")
  Optional<Channel> findByIdWithMessages(@Param("channelId") UUID channelId);
}
