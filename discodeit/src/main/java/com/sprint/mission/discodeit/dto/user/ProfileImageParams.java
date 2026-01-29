package com.sprint.mission.discodeit.dto.user;

import lombok.Builder;

@Builder
public record ProfileImageParams(
        byte[] bytes,
        String contentType,
        String filename
) {

}
