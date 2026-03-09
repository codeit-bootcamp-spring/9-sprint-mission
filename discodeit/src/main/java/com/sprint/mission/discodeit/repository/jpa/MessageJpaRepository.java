package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageJpaRepository extends JpaRepository<Message, UUID> {

  List<Message> findAllByChannelId(UUID channelId);

  void deleteAllByChannelId(UUID channelId);

}
