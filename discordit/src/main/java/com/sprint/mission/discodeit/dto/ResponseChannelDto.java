package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record ResponseChannelDto(
    String channelName,
    UUID channelId,
    ChannelType type,
    Instant createAt,
    Instant updateAt,
    String createUser,
    Map<String, UUID> accessibleUsers
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").withZone(ZoneId.of("Asia/Seoul"));

    @Override
    public String toString() {
        String result =  "====================\n" +
                "채널명 : " + channelName + "\n" +
                "채널ID : " + channelId + "\n" +
                "타입 : " + type + "\n" +
                "채널생성일 : " + FORMATTER.format(createAt) + "\n" +
                "채널수정일 : " + FORMATTER.format(updateAt) + "\n";

        String accessUserResult = "";
        if(type.equals(ChannelType.PRIVATE)) {
            try {
                accessUserResult = accessibleUsers.entrySet().stream()
                        .map(entry -> "멤버명 : " + entry.getKey() + "\n멤버 ID : " + entry.getValue() + "\n")
                        .collect(Collectors.joining());
            } catch (Exception ignore) {}
        }

        if(accessUserResult.isEmpty()) return result + "관리자: " + createUser;

        return result + "관리자: " + createUser + "\n" + accessUserResult;
    }
}
