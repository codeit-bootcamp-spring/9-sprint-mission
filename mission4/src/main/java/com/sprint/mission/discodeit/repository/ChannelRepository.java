package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  @Query("""
          SELECT DISTINCT c FROM Channel c 
          LEFT JOIN ReadStatus rs ON rs.channel = c 
          WHERE c.type = com.sprint.mission.discodeit.entity.ChannelType.PUBLIC 
          OR rs.user.id = :userId
      """)
  List<Channel> findAllAccessibleByUserId(@Param("userId") UUID userId);


  @EntityGraph(attributePaths = {"readStatuses", "readStatuses.user"})
  List<Channel> findAll();


}
