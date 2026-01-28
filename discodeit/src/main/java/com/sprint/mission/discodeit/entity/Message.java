package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Message extends BaseEntity {
    private String content;
    private UUID authorId; //
    private UUID channelId; //

    // [추가] 여러 개의 첨부파일(BinaryContent)을 참조하는 ID 리스트
    private List<UUID> attachmentIds = new ArrayList<>();

    public Message(String content, UUID authorId, UUID channelId) {
        super();
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
    }
}