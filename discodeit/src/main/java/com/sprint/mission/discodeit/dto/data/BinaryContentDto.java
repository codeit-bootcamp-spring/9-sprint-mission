package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;


public record BinaryContentDto (
   UUID id,
   String originalFileName,
   String contentType,
   Long size
 ) {}
