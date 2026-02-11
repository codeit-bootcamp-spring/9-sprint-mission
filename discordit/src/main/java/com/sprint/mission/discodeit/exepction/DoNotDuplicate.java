package com.sprint.mission.discodeit.exepction;

public class DoNotDuplicate extends RuntimeException {
    public DoNotDuplicate(String message) {
        super(message);
    }
}
