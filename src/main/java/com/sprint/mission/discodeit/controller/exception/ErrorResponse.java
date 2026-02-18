package com.sprint.mission.discodeit.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class ErrorResponse {
    //**ErrorResponse는 “에러가 났을 때 클라이언트에게 내려줄 JSON 형태”**를 정해주는 DTO야.
    private String code;
    private String message;
}
