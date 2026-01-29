package com.sprint.mission.discodeit.status;

import com.sprint.mission.discodeit.status.add.ReadStatus;

import java.util.Optional;
import java.util.UUID;

public interface ReadStatusInterface {
    void save(ReadStatus readStatus);
    Optional<ReadStatus> findBy(UUID userId, UUID channelId);
    //Optinal : 있을수도있고 없을수도있는 상태를 둘 다 정상적인 상태로 만듬 -> null 방지
}
