package com.sprint.mission.discodeit.exepction;

public class FailedCreate extends RuntimeException {
    public FailedCreate(String message) {
        super(message);
    }
}
