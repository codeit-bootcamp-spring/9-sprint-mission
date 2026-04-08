package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  /** 채널 메시지 최신순 조회 (작성자 프로필/상태 페치 조인) */
  @Query("""
      SELECT m FROM Message m
      JOIN FETCH m.author a
      LEFT JOIN FETCH a.profile
      LEFT JOIN FETCH a.status
      WHERE m.channel.id = :channelId
      ORDER BY m.createdAt DESC
      """)
  Slice<Message> findAllByChannelId(
      @Param("channelId") UUID channelId,
      Pageable pageable);

  /** 커서(createdAt) 이전 메시지 조회 (커서 기반 페이지네이션용) */
  @Query("""
      SELECT m FROM Message m
      JOIN FETCH m.author a
      LEFT JOIN FETCH a.profile
      LEFT JOIN FETCH a.status
      WHERE m.channel.id = :channelId
        AND m.createdAt < :cursor
      ORDER BY m.createdAt DESC
      """)
  Slice<Message> findAllByChannelIdAndCreatedAtBefore(
      @Param("channelId") UUID channelId,
      @Param("cursor") Instant cursor,
      Pageable pageable);

  /** 채널의 전체 메시지 조회 (채널 삭제 시 첨부파일 정리용) */
  List<Message> findAllByChannel_Id(UUID channelId);

  /** 채널의 최신 메시지 시각 조회 (채널 목록 lastMessageAt 표시용) */
  @Query("SELECT MAX(m.createdAt) FROM Message m WHERE m.channel.id = :channelId")
  Optional<Instant> findLastCreatedAtByChannelId(@Param("channelId") UUID channelId);

  /** 여러 채널의 최신 메시지 시각 일괄 조회 (채널 목록 N+1 방지) */
  @Query("SELECT m.channel.id, MAX(m.createdAt) FROM Message m WHERE m.channel.id IN :channelIds GROUP BY m.channel.id")
  List<Object[]> findLastCreatedAtByChannelIds(@Param("channelIds") List<UUID> channelIds);
}
