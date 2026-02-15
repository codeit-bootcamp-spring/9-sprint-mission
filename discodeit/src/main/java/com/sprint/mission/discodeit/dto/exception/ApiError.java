package com.sprint.mission.discodeit.dto.exception;

public record ApiError(
        String code
        , String message) {

}