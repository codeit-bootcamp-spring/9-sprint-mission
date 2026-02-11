package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.NonNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record FindChannelDto(
        String channelInfo,
        ChannelType channelType,
        Instant lastMessage
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").withZone(ZoneId.of("Asia/Seoul"));

    public @NonNull String getInfo() {
        return channelInfo +  "\n마지막 메시지 : " + FORMATTER.format(lastMessage);
    }
}
