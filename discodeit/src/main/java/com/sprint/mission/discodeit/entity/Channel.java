package com.sprint.mission.discodeit.entity;
import lombok.Getter;

/**
 * 채널 엔티티 클래스
 * 메시지가 교환되는 채널을 나타내며 PUBLIC 또는 PRIVATE 타입을 가집니다.
 */
@Getter
public class Channel extends BaseEntity {
    /** 채널 타입 (PUBLIC/PRIVATE) */
    private final ChannelType type;

    /** 채널 이름 */
    private String name;

    /** 채널 설명 */
    private String description;

    /**
     * 채널 생성자
     *
     * @param type 채널 타입 (PUBLIC 또는 PRIVATE)
     * @param name 채널 이름
     * @param description 채널 설명
     */
    public Channel(ChannelType type, String name, String description) {
        super();
        this.type = type;
        this.name = name;
        this.description = description;
    }

    /**
     * 채널 정보를 업데이트합니다.
     * null이 아니고 기존 값과 다른 경우에만 업데이트되며, 실제 변경이 있을 때만 수정일시가 갱신됩니다.
     *
     * @param name 새로운 채널 이름 (null이면 업데이트하지 않음)
     * @param description 새로운 채널 설명 (null이면 업데이트하지 않음)
     */
    public void update(String name, String description) {
        boolean updated = false;
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            updated = true;
        }
        if (description != null && !description.equals(this.description)) {
            this.description = description;
            updated = true;
        }
        if (updated) updateTimeStamp();
    }
}