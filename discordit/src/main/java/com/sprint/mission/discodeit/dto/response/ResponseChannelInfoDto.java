package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

public record ResponseChannelInfoDto(
        ChannelType type,
        UUID channelId,
        String channelName,
        String creator,
        Map<UUID, String> accessibleUser,
        Instant createAt,
        Instant updateAt
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").withZone(ZoneId.of("Asia/Seoul"));

    @Override
    public String toString() {
        StringBuilder accessibleUserList = new StringBuilder();
        accessibleUser.forEach((key, value) ->
                accessibleUserList
                        .append("ID: ").append(key).append("\n")
                        .append("사용자명: ").append(value).append("\n")
        );
        return "채널 타입: " + type.toString() + "\n"
                + "채널 ID: " + channelId.toString() + "\n"
                + "채널명: " + channelName + "\n"
                + "생성자: " + creator + "\n"
                + accessibleUserList
                + "생성일: " + FORMATTER.format(createAt) + "\n"
                + "수정일: " + FORMATTER.format(updateAt) + "\n";
    }
}
