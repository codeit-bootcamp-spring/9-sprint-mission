package com.sprint.mission.discodeit;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Getter
public class UserState {
	private String userName = "";
	private UUID userId = null;

	public void userState(String username, UUID userId) {
		this.userName = username;
		this.userId = userId;
	}
	public void userState(String username) {
		this.userName = "";
		this.userId = null;
	}
}
