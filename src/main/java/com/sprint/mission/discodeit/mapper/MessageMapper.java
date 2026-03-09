package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper {

  private final BinaryContentRepository binaryContentRepository;

  public MessageMapper(BinaryContentRepository binaryContentRepository) {
    this.binaryContentRepository = binaryContentRepository;
  }

  public MessageDto toDto(Message message) {
    List<BinaryContentDto> attachments = message.getAttachments().stream()
        .map(this::toBinaryContentDto)
        .collect(Collectors.toList());

    var author = message.getAuthor();

    BinaryContentDto profileDto = null;
    if (author.getProfileId() != null) {
      BinaryContent profile = binaryContentRepository.findById(author.getProfileId())
          .orElse(null);
      if (profile != null) {
        profileDto = toBinaryContentDto(profile);
      }
    }

    UserDto authorDto = new UserDto(
        author.getId(),
        author.getUsername(),
        author.getEmail(),
        profileDto,
        author.isOnline() // 이제 여기서 바로 호출 가능
    );

    return new MessageDto(
        message.getId(),
        message.getChannel().getId(),
        authorDto,
        message.getContent(),
        attachments,
        message.getCreatedAt(),
        message.getUpdatedAt()
    );
  }

  private BinaryContentDto toBinaryContentDto(BinaryContent bc) {
    return new BinaryContentDto(
        bc.getId(),
        bc.getFileName(),
        bc.getSize(),
        bc.getContentType(),
        bc.getCreatedAt()
    );
  }
}