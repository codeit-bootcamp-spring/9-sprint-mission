package com.sprint.mission.discodeit.exepction;

public class NotAllowedFileExtOrSize extends RuntimeException {
    public NotAllowedFileExtOrSize(String message) {
        super(message);
    }
}
