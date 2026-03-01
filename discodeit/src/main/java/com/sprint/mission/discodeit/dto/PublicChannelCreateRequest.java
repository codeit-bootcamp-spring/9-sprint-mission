package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PublicChannelCreateRequest {

  @NotBlank(message = "채널 이름은 필수입니다.")
  private String name;
  private String description;
}