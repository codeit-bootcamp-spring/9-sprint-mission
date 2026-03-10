package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// JpaRepository를 상속받아 읽음 상태에 대한 데이터 조작을 자동화합니다.
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    // 특정 채널에 속한 읽음 상태 기록을 모두 가져옵니다.
    List<ReadStatus> findAllByChannelId(UUID channelId);

    // 특정 채널에 속한 읽음 상태 기록을 한 번에 삭제합니다.
    void deleteAllByChannelId(UUID channelId);

    // [추가됨] 특정 유저가 가진 모든 읽음 상태 기록을 가져오는 쿼리 메서드입니다.
    List<ReadStatus> findAllByUserId(UUID userId);

    // [추가됨] 특정 유저가 특정 채널에 대해 이미 읽음 상태를 가지고 있는지 데이터베이스 레벨에서 초고속으로 확인하는 메서드입니다.
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
}