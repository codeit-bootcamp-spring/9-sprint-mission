package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    List<Channel> findAllByChannelType(ChannelType channelType);

    @Query("SELECT c FROM Channel c JOIN c.participantIds p WHERE c.channelType = 'PRIVATE' AND :userId IN (p)")
    List<Channel> findPrivateChannelsByUserId(@Param("userId") UUID userId);
}