package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        // 1. 스프링 엔진을 실행하고 그 결과(Context)를 변수에 담습니다.
        ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 2. 우리가 만든 UserService 부품이 스프링 컨테이너 안에 잘 들어있는지 확인합니다.
        UserService userService = context.getBean(UserService.class);

        // 3. 부품이 비어있지 않고 잘 조립되었다면 로그가 찍힐 겁니다.
        System.out.println("========================================");
        System.out.println("부품 조립 결과: " + (userService != null ? "성공!" : "실패..."));
        System.out.println("========================================");
    }
}