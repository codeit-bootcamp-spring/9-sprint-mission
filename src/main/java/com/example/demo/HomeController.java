package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    // 루트("/") 접속 시
    @GetMapping("/")
    public String home() {
        return "Hello, Discodeit 서버가 정상적으로 실행 중입니다.";
    }
}
