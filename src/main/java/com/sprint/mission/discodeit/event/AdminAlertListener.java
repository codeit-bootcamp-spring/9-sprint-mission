package com.sprint.mission.discodeit.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdminAlertListener {

    @Async
    @EventListener
    public void on(AdminErrorAlertEvent event) {
        String alertMessage = String.format("[서버 장애 알림]\n- 에러 타입: %s\n- 에러 내용: %s",
        event.errorType(), event.errorMessage());
        log.info("관리자에게 에러 알림을 전송했습니다: \n{}", alertMessage);
    }
}
