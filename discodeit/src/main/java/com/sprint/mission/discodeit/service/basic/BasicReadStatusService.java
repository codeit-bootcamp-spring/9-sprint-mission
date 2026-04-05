package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Transactional
  @Override
  public ReadStatusResponse create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));

    ReadStatus readStatus = readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId)
        .orElseGet(() -> {
          ReadStatus createdReadStatus = new ReadStatus(user, channel, request.lastReadAt());
          return readStatusRepository.save(createdReadStatus);
        });

    return readStatusMapper.toResponse(readStatus);
  }

  @Override
  public ReadStatusResponse find(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new DiscodeitException(ErrorCode.READ_STATUS_NOT_FOUND,
            Map.of("readStatusId", readStatusId)));
    return readStatusMapper.toResponse(readStatus);
  }

  @Override
  public List<ReadStatusResponse> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUser_Id(userId).stream()
        .map(readStatusMapper::toResponse)
        .toList();
  }

  @Transactional
  @Override
  public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new DiscodeitException(ErrorCode.READ_STATUS_NOT_FOUND,
            Map.of("readStatusId", readStatusId)));
    readStatus.update(request.newLastReadAt());
    return readStatusMapper.toResponse(readStatus);
  }

  @Transactional
  @Override
  public void delete(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new DiscodeitException(ErrorCode.READ_STATUS_NOT_FOUND,
            Map.of("readStatusId", readStatusId)));
    readStatusRepository.delete(readStatus);
  }
}
