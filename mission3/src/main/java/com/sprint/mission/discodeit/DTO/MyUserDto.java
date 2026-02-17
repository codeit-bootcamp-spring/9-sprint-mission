package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public class MyUserDto {
    public record BasicInfo(
            String username,
        String email,
        String password
            ){}
    public record profileInfo(
            UUID profileId,
            String fileName,
           String contentType

    ){}
    public record FindInfo(
            UUID id,
            String username,
            String email,
            boolean isOnline

    ){}
    public record UpdateInfo(
            UUID userid,
            String newName,
            String newEmail,
            String password,
            String newProfileFilePath,
            String newFileContent
    ){}
    public record AllInfo(
           BasicInfo basicInfo,
           profileInfo profileInfo

    ){}

}
