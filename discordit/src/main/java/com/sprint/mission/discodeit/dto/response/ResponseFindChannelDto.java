package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record ResponseFindChannelDto(
        ResponseChannelDto channelInfo,
        ChannelType channelType,
        Instant lastMessage
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").withZone(ZoneId.of("Asia/Seoul"));

    public String getInfo() {
        if(lastMessage == null) return channelInfo.toString();
        return channelInfo +  "\n마지막 메시지 : " + FORMATTER.format(lastMessage);
    }
}
