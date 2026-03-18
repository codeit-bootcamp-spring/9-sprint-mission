package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass // 진짜 테이블 아니고 템플릿일 뿐이야 ! 라고 선언
@EntityListeners(AuditingEntityListener.class) //자동시간 기록
public abstract class BaseEntity {

  @Id // 이 필드가 이 데이터의 pk(주민번호)라는 뜻
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @CreatedDate // 데이터가 처음 생길 때 시간 찍어줘
  @Column(columnDefinition = "timestamp with time zone", updatable = false, nullable = false)
  private Instant createdAt;

}