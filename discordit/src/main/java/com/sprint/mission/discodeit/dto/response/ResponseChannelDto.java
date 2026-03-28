package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

public record ResponseChannelDto(
    String name,
    String description,
    UUID channelId,
    ChannelType type,
    Instant createAt,
    Instant updateAt,
    Map<String, UUID> accessibleUsers
) {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy년 MM월 dd일 HH시 mm분 ss초").withZone(ZoneId.of("Asia/Seoul"));

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    StringBuilder userList = new StringBuilder();

    accessibleUsers.forEach(
        (name, id) -> userList.append("    - 참가자명: " + name + "\n    - 참가자ID: " + id));

    if (type.equals(ChannelType.PUBLIC)) {
      return result.append("====================\n" +
          "타입 : " + type + "\n" +
          "채널ID : " + channelId + "\n" +
          "채널명 : " + name + "\n" +
          "채널설명 : " + description + "\n" +
          "채널생성일 : " + FORMATTER.format(createAt) + "\n" +
          "채널수정일 : " + FORMATTER.format(updateAt) + "\n").toString();
    } else {
      return result.append("====================\n" +
          "타입 : " + type + "\n" +
          "채널ID : " + channelId + "\n" +
          "참여자 리스트 : \n" +
          userList +
          "채널생성일 : " + FORMATTER.format(createAt) + "\n").toString();
    }
  }
}
