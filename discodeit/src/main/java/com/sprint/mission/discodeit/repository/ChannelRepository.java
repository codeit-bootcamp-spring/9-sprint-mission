package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  @EntityGraph(attributePaths = {"participants"})
  @Query("SELECT DISTINCT c FROM Channel c JOIN c.participants p WHERE p.id = :userId")
  List<Channel> findAllByUserId(@Param("userId") UUID userId);

  @EntityGraph(attributePaths = {"participants"})
  @Query("SELECT DISTINCT c FROM Channel c LEFT JOIN c.participants p " +
      "WHERE p.id = :userId OR c.type = 'PUBLIC'")
  List<Channel> findAllByUserIdOrPublic(@Param("userId") UUID userId);
}