package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    List<Channel> findAllByChannelType(ChannelType type);

    List<Channel> findByParticipantIdsContainingAndChannelType(UUID userId, ChannelType type);

    Optional<Channel> findByNameAndChannelType(String name, ChannelType type);
}