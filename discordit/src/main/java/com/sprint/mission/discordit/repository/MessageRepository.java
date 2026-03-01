package com.sprint.mission.discordit.repository;

import com.sprint.mission.discordit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

  Message save(Message message);

  Optional<Message> findById(UUID id);

  List<Message> findAllByChannelId(UUID channelId);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  void deleteAllByChannelId(UUID channelId);
}
