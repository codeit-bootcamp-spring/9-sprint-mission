package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Discodeit 애플리케이션의 메인 클래스
 *
 * Discord와 유사한 메시징 플랫폼으로, 다음 기능을 제공합니다:
 * - 사용자 관리 (회원가입, 로그인, 프로필 이미지)
 * - 채널 관리 (공개 채널, 비공개 채널)
 * - 메시지 관리 (메시지 작성, 첨부파일)
 * - 읽기 상태 추적
 * - 사용자 온라인 상태 관리
 */
@SpringBootApplication
public class DiscodeitApplication {

	/**
	 * 애플리케이션 시작점
	 *
	 * @param args 명령행 인수
	 */
	public static void main(String[] args) {
		SpringApplication.run(DiscodeitApplication.class, args);
	}
}
