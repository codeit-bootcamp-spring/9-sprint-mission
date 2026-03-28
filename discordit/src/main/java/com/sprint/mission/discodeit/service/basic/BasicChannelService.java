package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.RequestChannelDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseFindChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exepction.global.Forbidden;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;

  @Override
  public boolean isPresent(UUID id) {
    return channelRepository.isPresentChannel(id);
  }

  @Override
  public ResponseChannelDto create(RequestChannelDto requestDto) {
    return channelRepository.save(requestDto);
  }

  @Override
  public ResponseFindChannelDto find(UUID channelId) {
    ResponseChannelDto channelInfo = channelRepository.findChannel(channelId);
    ChannelType channelType = channelRepository.getChannelType(channelId);
    Instant lastMessageTime = null;
    try {
      lastMessageTime = messageRepository.getLastMessageInChannel(channelId);
    } catch (Exception ignore) {
    }

    return new ResponseFindChannelDto(channelInfo, channelType, lastMessageTime);
  }

  @Override
  public List<ResponseChannelDto> findAllPrivateChannel(UUID userId) {
    return channelRepository.findAllPrivateChannel(userId);
  }

  @Override
  public List<ResponseChannelDto> findAll(UUID userId) {
    return channelRepository.findAllChannel(userId);
  }

  @Override
  public ResponseChannelDto update(UUID channelId, RequestUpdateChannelDto requestDto) {
    if (!isPresent(channelId)) {
      throw new NotFound("The channel does not exist.");
    }

    if (channelRepository.getChannelType(channelId).equals(ChannelType.PRIVATE)) {
      throw new Forbidden("Failed: Private channel cannot update!");
    }

    return channelRepository.save(channelId, requestDto);
  }

  @Override
  public boolean delete(UUID id) {
    return channelRepository.deleteChannel(id);
  }

  @Override
  public boolean includePrivateChannel(String channelName, String username, UUID userId) {
    channelRepository.includePrivateChannel(channelName, username, userId);
    return true;
  }

  @Override
  public void excludePrivateChannel(String channelName, String username) {
    channelRepository.excludePrivateChannel(channelName, username);
  }
}
