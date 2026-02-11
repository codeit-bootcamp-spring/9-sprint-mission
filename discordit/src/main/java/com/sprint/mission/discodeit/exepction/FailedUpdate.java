package com.sprint.mission.discodeit.exepction;

public class FailedUpdate extends RuntimeException {
    public FailedUpdate(String message) {
        super(message);
    }
}
