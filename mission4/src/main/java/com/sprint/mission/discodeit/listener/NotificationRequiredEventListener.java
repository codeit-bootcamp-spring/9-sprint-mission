package com.sprint.mission.discodeit.listener;

/**
 * (사용 안 함) 예전에는 이 클래스가 MessageCreatedEvent/RoleUpdatedEvent를 직접 받아
 * BasicNotificationService를 호출했습니다. 그런데 같은 이벤트가
 * KafkaProduceRequiredEventListener를 통해 Kafka로도 발행되고, 그걸 다시
 * NotificationRequiredTopicListener가 소비하면서 BasicNotificationService의
 * 같은 메서드를 한 번 더 호출하고 있었습니다. 그 결과 메시지 하나, 역할 변경 하나당
 * 알림이 DB에 2번 저장되고 SSE("notifications.created")도 2번씩 나가는 중복 버그가
 * 있었습니다.
 *
 * docker-compose 상 app 서비스가 replicas: 3으로 여러 인스턴스로 뜨는 구조이고,
 * Kafka 컨슈머 그룹("notification-group")을 쓰면 같은 메시지를 클러스터 전체에서
 * 정확히 한 인스턴스만 처리하게 되므로, 다중 인스턴스 환경에서는 Kafka 경로가
 * 알림 생성의 유일한 경로가 되는 게 맞습니다. 그래서 이 클래스의 직접 처리 로직은
 * 제거했고, 알림 생성은 KafkaProduceRequiredEventListener → Kafka →
 * NotificationRequiredTopicListener 경로로만 일어납니다.
 *
 * 참고: 클래스 파일 자체는 삭제 권한이 없어 내용만 비웠습니다. @Component가 없어
 * Spring이 빈으로 등록하지 않으므로 애플리케이션 동작에는 영향이 없습니다.
 */
public class NotificationRequiredEventListener {

}
