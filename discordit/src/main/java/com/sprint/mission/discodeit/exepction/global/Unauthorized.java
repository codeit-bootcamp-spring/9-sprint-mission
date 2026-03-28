package com.sprint.mission.discodeit.exepction.global;

public class Unauthorized extends RuntimeException {
    public Unauthorized(String message) {
        super(message);
    }
}
