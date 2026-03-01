package com.sprint.mission.discordit.repository;

import com.sprint.mission.discordit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

  Channel save(Channel channel);

  Optional<Channel> findById(UUID id);

  List<Channel> findAll();

  boolean existsById(UUID id);

  void deleteById(UUID id);
}
