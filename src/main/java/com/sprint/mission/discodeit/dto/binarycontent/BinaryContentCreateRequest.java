package com.sprint.mission.discodeit.dto.binarycontent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinaryContentCreateRequest {
    private String fileName;
    private String contentType;
    private byte[] data;
}
