package com.sprint.mission.discodeit.DTO;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {
    public record PublicDto(
            UUID userId,
            String name,
            String description
    ){}
    public record PrivateDto(
            UUID userId,
            List<UUID> userlist
    ){}
    public record FindDto(
            UUID id,
            String name,
            ChannelType type,
            Instant lastedAt,
            List<UUID> userid
    ){}

    public record UpdateDto(
            UUID Id,
            String newName,
            String newDescription
    ){}
}
