package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDto {

  private UUID id;
  private String type;
  private String name;
  private String description;
  private List<UUID> participantIds;
  private Instant lastMessageAt;
}