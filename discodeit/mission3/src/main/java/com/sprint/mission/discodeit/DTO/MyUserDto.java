package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public class MyUserDto {
    public record BasicInfo(
            String username,
        String email,
        String password
            ){}
    public record profileInfo(
           UUID ownerId,
           String filePath,
           String fileContent

    ){}
    public record FindInfo(
            UUID id,
            String username,
            String email,
            boolean isOnline

    ){}
    public record AllInfo(
           BasicInfo basicInfo,
           profileInfo profileInfo
    ){}

}
