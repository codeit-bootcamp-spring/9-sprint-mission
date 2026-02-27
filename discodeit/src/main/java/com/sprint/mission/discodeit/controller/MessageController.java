package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDeleteRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.MessageView;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @Override
  public MessageView create(MessageCreateRequest request, List<MultipartFile> attachments) {
    var files = attachments == null ? List.<MultipartFile>of() : attachments;

    var attachmentParams = files.stream()
        .filter(file -> !file.isEmpty())
        .map(file -> {
          try {
            return new MessageCreateRequest.AttachmentParams(
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename()
            );
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .toList();

    var params = new MessageCreateRequest.Params(request.params().content(), attachmentParams);
    var merged = new MessageCreateRequest(request.channelId(), request.senderId(), params);

    return messageService.create(merged);
  }

  @Override
  public MessageView update(UUID messageId, MessageUpdateRequest.Params params) {
    return messageService.update(new MessageUpdateRequest(messageId, params));
  }

  @Override
  public MessageView findById(UUID messageId) {
    return messageService.findById(messageId);
  }

  @Override
  public List<MessageView> findAllByChannelId(UUID channelId) {
    return messageService.findAllByChannelId(channelId);
  }

  @Override
  public ResponseEntity<Void> delete(UUID messageId) {
    messageService.delete(new MessageDeleteRequest(messageId));
    return ResponseEntity.noContent().build();
  }
}
