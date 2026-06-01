package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.entity.User;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  @Query("select distinct c from Channel c " +
      "left join fetch c.readStatuses rs " +
      "where c.type = 'PUBLIC' " +
      "or (c.type = 'PRIVATE' and rs.user.id = :userId)")
  List<Channel> findAllByUserId(@Param("userId") UUID userId);

  @Query("SELECT DISTINCT c FROM Channel c JOIN FETCH c.readStatuses " +
      "WHERE c.id IN (SELECT rs.channel.id FROM ReadStatus rs WHERE rs.user.id = :userId)")
  List<Channel> findAllWithParticipantsByUserId(@Param("userId") UUID userId);
}
