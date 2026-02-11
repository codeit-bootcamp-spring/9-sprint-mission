package com.sprint.mission.discodeit.exepction;

public class FailedInit extends RuntimeException {
    public FailedInit(String message) {
        super(message);
    }
}
