package com.sprint.mission.discodeit.exepction;

public class FailedFound extends RuntimeException {
    public FailedFound(String message) {
        super(message);
    }
}
