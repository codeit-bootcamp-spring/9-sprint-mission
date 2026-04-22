package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entity.Message;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Slice;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.time.Instant;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("SELECT DISTINCT m FROM Message m " +
      "JOIN FETCH m.author " +
      "WHERE m.channel.id = :channelId " +
      "AND (cast(:cursor as Instant) IS NULL OR m.createdAt < :cursor) " +
      "ORDER BY m.createdAt DESC")
  Slice<Message> findMessagesNoOffset(
      @Param("channelId") UUID channelId,
      @Param("cursor") Instant cursor,
      Pageable pageable);
}