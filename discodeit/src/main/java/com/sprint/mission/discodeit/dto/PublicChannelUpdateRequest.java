package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PublicChannelUpdateRequest {

  @NotBlank(message = "수정할 채널 이름은 필수입니다.")
  private String name;

  private String description;
}