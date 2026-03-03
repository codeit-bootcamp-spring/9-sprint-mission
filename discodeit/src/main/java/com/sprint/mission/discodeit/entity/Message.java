package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.util.List;
import java.util.UUID;

/**
 * 메시지 엔티티 클래스
 * 채널 내에서 사용자가 작성한 메시지를 나타내며 첨부파일을 포함할 수 있습니다.
 */
@Getter
public class Message extends BaseEntity {
    /** 메시지 내용 */
    private String content;

    /** 메시지가 속한 채널 ID */
    private final UUID channelId;

    /** 메시지 작성자 ID */
    private final UUID authorId;

    /** 첨부파일 ID 목록 (BinaryContent 참조) */
    private final List<UUID> attachmentIds;

    /**
     * 메시지 생성자
     *
     * @param content 메시지 내용
     * @param channelId 메시지가 속한 채널 ID
     * @param authorId 메시지 작성자 ID
     * @param attachmentIds 첨부파일 ID 목록
     */
    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        super();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds;
    }

    /**
     * 메시지 내용을 업데이트합니다.
     * null이 아닌 값만 업데이트되며, 수정일시가 갱신됩니다.
     *
     * @param content 새로운 메시지 내용 (null이면 업데이트하지 않음)
     */
    public void update(String content) {
        if (content != null) {
            this.content = content;
            updateTimeStamp();
        }
    }
}