package com.sprint.mission.discodeit.exepction;

public class DoNotUpdatePrivateChannel extends RuntimeException {
    public DoNotUpdatePrivateChannel(String message) {
        super(message);
    }
}
