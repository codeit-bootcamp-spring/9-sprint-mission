package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.ResponseBinaryContentDto;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BasicBinaryContentService basicBinaryContentService;

  @RequestMapping(method = RequestMethod.GET)
  public List<ResponseBinaryContentDto> handleFindBinaryContents(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds
  ) {
    return basicBinaryContentService.findAllByIdIn(binaryContentIds);
  }

  @RequestMapping(value = "{binaryContentId}", method = RequestMethod.GET)
  public List<ResponseBinaryContentDto> handleFindBinaryContent(
      @PathVariable List<UUID> binaryContentId
  ) {
    return basicBinaryContentService.findAllByIdIn(binaryContentId);
  }
}