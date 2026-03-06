package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.entity.User;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  @Query("select c from Channel c " +
      "join ReadStatus rs on c.id = rs.channel.id " +
      "where rs.user.id = :userId")
  List<Channel> findAllByUserId(UUID userId);
}
