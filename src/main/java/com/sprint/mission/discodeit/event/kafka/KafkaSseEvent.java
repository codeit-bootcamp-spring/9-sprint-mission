package com.sprint.mission.discodeit.event.kafka;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaSseEvent {

    public enum Type {BROADCAST, SEND}

    private Type type;
    private String eventName;
    private Object data;
    private List<UUID> receiverIds;
}
