package com.sprint.mission.discodeit.exepction;

public class FailedDelete extends RuntimeException {
    public FailedDelete(String message) {
        super(message);
    }
}
