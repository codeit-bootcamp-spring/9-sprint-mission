package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

// Java의 Enum은 기본적으로 Serializable을 상속받지만 명시해주는 것이 일관성에 좋습니다.
public enum ChannelType implements Serializable {
    TEXT,
    VOICE
}