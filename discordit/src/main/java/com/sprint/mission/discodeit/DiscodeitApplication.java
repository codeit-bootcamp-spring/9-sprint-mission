package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.app.router.Router;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
@RequiredArgsConstructor
public class DiscodeitApplication {

	@Bean
	public Scanner scanner() {
		return new Scanner(System.in);
	}

	public static void main(String[] args) {
		var context = SpringApplication.run(DiscodeitApplication.class, args);
		Router router = context.getBean(Router.class);
		System.out.println("프로그램을 실행합니다.");
		router.route();
	}
}

