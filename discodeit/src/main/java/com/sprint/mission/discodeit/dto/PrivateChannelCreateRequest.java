package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PrivateChannelCreateRequest {

  private String name;
  private String description;
  private List<UUID> participantIds;
}