package com.sprint.mission.discodeit.DTO;

import java.util.List;
import java.util.UUID;

public class MessageDto {
    public record CreateMessage(
    String content,
    UUID channelId,
    UUID authorId,
    List<UUID> attachmentId
    ){}

    public record findMessage(
        UUID id,
        String content,
        UUID authorId,
        List<fileInfo> fileInfo
){}
    public record fileInfo(
            UUID id,
            String fileName,
            String filePath
    ){}


    public record UpdateMessage(
       UUID Id,
       String newContent,
       List<UUID> newAttachmentId
    ){}

}
