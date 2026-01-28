package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class BinaryContent extends BaseEntity {
    public BinaryContent(String dummy) {
        super();
    }
}