package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.request.RequestCreateReadStatusDto;
import com.sprint.mission.discodeit.dto.response.ResponseReadStatus;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    prefix = "discodeit.repository",
    name = "type",
    havingValue = "jcf",
    matchIfMissing = true
)
public class JCFReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatus> idReadStatusMap = new ConcurrentHashMap<>();
  private final Map<UUID, List<ReadStatus>> userIdReadStatusMap = new ConcurrentHashMap<>();
  private final Map<UUID, List<ReadStatus>> channelIdReadStatusMap = new ConcurrentHashMap<>();

  @Override
  public List<ResponseReadStatus> create(RequestCreateReadStatusDto request) {
    List<ResponseReadStatus> result = new ArrayList<>();
    for (UUID channelId : request.channelIds()) {
      result.add(this.create(request.userId(), channelId));
    }
    return result;
  }

  private ResponseReadStatus create(UUID userId, UUID channelId) {
    ReadStatus readStatus = new ReadStatus(userId, channelId);
    idReadStatusMap.put(readStatus.getId(), readStatus);
    userIdReadStatusMap.computeIfAbsent(userId, id -> new ArrayList<>()).add(readStatus);
    channelIdReadStatusMap.computeIfAbsent(userId, id -> new ArrayList<>()).add(readStatus);
    return response(readStatus);
  }

  private ResponseReadStatus response(ReadStatus readStatus) {
    return new ResponseReadStatus(readStatus.getId(), readStatus.getUserId(),
        readStatus.getChannelId(), readStatus.getCreateAt(), readStatus.getUpdateAt(),
        readStatus.getLastReadAt());
  }

  @Override
  public Instant find(UUID id) {
    return idReadStatusMap.get(id).getUpdateAt();
  }

  @Override
  public List<ResponseReadStatus> findAllByUserId(UUID userId) {
    List<ResponseReadStatus> result = new ArrayList<>();

    for (ReadStatus readStatus : userIdReadStatusMap.get(userId)) {
      result.add(response(readStatus));
    }
    return result;
  }

  @Override
  public ResponseReadStatus update(UUID userId, UUID channelId) {
    ReadStatus temp = userIdReadStatusMap.get(userId).stream()
        .filter(readStatus -> readStatus.getChannelId().equals(channelId)).findFirst().orElse(null);

      if (temp == null) {
          throw new NotFound("상태값을 찾지 못했습니다.");
      }

    temp.updateReadAt();
    return response(temp);
  }

  @Override
  public boolean delete(UUID id) {
    ReadStatus temp = idReadStatusMap.remove(id);
    userIdReadStatusMap.remove(temp.getUserId());
    channelIdReadStatusMap.remove(temp.getChannelId());
    return true;
  }

  @Override
  public void deleteForChannel(UUID channelId) {
    List<ReadStatus> temp = channelIdReadStatusMap.get(channelId);
    temp.forEach(readStatus -> delete(readStatus.getId()));
  }

  @Override
  public void deleteForUser(UUID userId) {
    List<ReadStatus> temp = userIdReadStatusMap.get(userId);
    temp.forEach(readStatus -> delete(readStatus.getId()));
  }
}
