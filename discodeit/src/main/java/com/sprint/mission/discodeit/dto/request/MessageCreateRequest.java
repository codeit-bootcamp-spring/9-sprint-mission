// 4. MessageCreateRequest.java
package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record MessageCreateRequest(String content, UUID authorId, UUID channelId) {

}