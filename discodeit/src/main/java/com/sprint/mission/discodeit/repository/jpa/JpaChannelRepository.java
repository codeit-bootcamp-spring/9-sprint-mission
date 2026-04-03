package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;


public interface JpaChannelRepository extends JpaRepository<Channel, UUID> {

}
