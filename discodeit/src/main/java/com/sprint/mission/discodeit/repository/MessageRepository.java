package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    // N+1 문제 해결: 연관된 엔티티들을 한 번의 JOIN 쿼리로 즉시 로딩합니다.
    @EntityGraph(attributePaths = {"author", "attachments"})
    Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

    void deleteAllByChannelId(UUID channelId);
}