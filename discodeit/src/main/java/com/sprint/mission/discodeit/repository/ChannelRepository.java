package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  /**
   * [수정] 유저가 참여 중인 채널을 찾되, 참여자 목록(participants) 전체를 안전하게 가져옵니다. JOIN FETCH 대신 서브쿼리나 JOIN을 통해 대상을
   * 필터링하고, @EntityGraph로 전체 컬렉션을 로드합니다.
   */
  @EntityGraph(attributePaths = {"participants"})
  @Query("SELECT DISTINCT c FROM Channel c JOIN c.participants p WHERE p.id = :userId")
  List<Channel> findAllByUserId(@Param("userId") UUID userId);

  /**
   * [수정] 유저가 참여 중이거나 공개된 채널을 조회합니다. 데이터 오염 방지를 위해 필터링 조건과 FETCH 로드 조건을 분리했습니다.
   */
  @EntityGraph(attributePaths = {"participants"})
  @Query("SELECT DISTINCT c FROM Channel c LEFT JOIN c.participants p " +
      "WHERE p.id = :userId OR c.type = 'PUBLIC'")
  List<Channel> findAllByUserIdOrPublic(@Param("userId") UUID userId);
}