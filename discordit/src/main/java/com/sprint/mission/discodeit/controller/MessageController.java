package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.RequestCreateBinaryContentDto;
import com.sprint.mission.discodeit.dto.request.RequestCreateMessageDto;
import com.sprint.mission.discodeit.dto.response.ResponseMessageDto;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final BasicBinaryContentService binaryContentService;
  private final ChannelService channelService;
  private final MessageService messageService;
  private final UserService userService;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseMessageDto handleCreateMessage(
      @RequestParam("channelId") UUID channelId,
      @RequestParam("userId") UUID userId,
      @RequestParam("messageCreateRequest") String content,
      @RequestParam(value = "attachments", required = false) List<MultipartFile> files
  ) throws IOException {
    List<UUID> binaryContentIds = new ArrayList<>();

    if (!channelService.isPresent(channelId)) {
      throw new NotFound("This channel is not present");
    }

    if (!userService.isPresent(userId)) {
      throw new NotFound("This user is not present");
    }

    if (files == null || files.isEmpty()) {
      RequestCreateMessageDto requestMessageDto = new RequestCreateMessageDto(content, channelId,
          userId, null);
      return messageService.create(requestMessageDto);
    }

    for (MultipartFile file : files) {
      String fileName = file.getOriginalFilename();
      String extension = fileName.substring(fileName.lastIndexOf("."));
      if (List.of(".jpg", ".jpeg", ".png").contains(extension)) {
        byte[] bytes = file.getBytes();
        RequestCreateBinaryContentDto requestBinaryContentDto = new RequestCreateBinaryContentDto(
            file.getContentType(), file.getOriginalFilename(), bytes);
        binaryContentIds.add(binaryContentService.create(requestBinaryContentDto));
      }
    }

    RequestCreateMessageDto requestMessageDto = new RequestCreateMessageDto(content, channelId,
        userId, binaryContentIds);
    return messageService.create(requestMessageDto);
  }

  @RequestMapping(value = "{messageId}", method = RequestMethod.PATCH)
  public ResponseMessageDto handleUpdateMessage(
      @PathVariable UUID messageId,
      @RequestParam("userId") UUID userId,
      @RequestParam("new_content") String newContent
  ) {
    return messageService.update(userId, messageId, newContent);
  }

  @RequestMapping(value = "{messageId}", method = RequestMethod.DELETE)
  public ResponseEntity<String> handleDeleteMessage(
      @PathVariable UUID messageId,
      @RequestParam("userId") UUID userId
  ) {
    if (messageService.delete(userId, messageId)) {
      return new ResponseEntity<>("Success", HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<ResponseMessageDto> handleFindMessage(
      @RequestParam("channelId") UUID channelId,
      @RequestParam("userId") UUID userId
  ) {
    channelService.find(channelId, userId);
    return messageService.findAllInChannel(channelId);
  }
}