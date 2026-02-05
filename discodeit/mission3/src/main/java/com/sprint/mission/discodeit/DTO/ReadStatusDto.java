package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public class ReadStatusDto {
    public record CreateDto(
            UUID userId,
            UUID channelId
    ){}


    public record UpdateDto(
            UUID Id,
            UUID userId,
            UUID channelId
    ){}
}
