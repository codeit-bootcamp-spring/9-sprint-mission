package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestDeleteUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestFindUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class JCFUserStatusRepository implements UserStatusRepository {

  private final Map<UUID, UserStatus> idMap = new ConcurrentHashMap<>();
  private final Map<UUID, UserStatus> userIdMap = new ConcurrentHashMap<>();
  private final Map<String, UserStatus> usernameMap = new ConcurrentHashMap<>();

  @Override
  public boolean create(RequestCreateUserStatusDto requestDto) {
    UserStatus userStatus = new UserStatus(requestDto.id(), requestDto.name());
    idMap.put(userStatus.getId(), userStatus);
    userIdMap.put(requestDto.id(), userStatus);
    usernameMap.put(requestDto.name(), userStatus);

    return true;
  }

  @Override
  public Boolean find(RequestFindUserStatusDto requestDto) {
    if (!userIdMap.containsKey(requestDto.id())) {
      return null;
    }
    return userIdMap.get(requestDto.id()).isOnline();
  }

  @Override
  public List<Boolean> findAll(List<RequestFindUserStatusDto> requestDto) {
    List<Boolean> result = new ArrayList<>();
    requestDto.forEach(req -> result.add(find(req)));
    return result;
  }

  @Override
  public UserStatus update(RequestUpdateUserStatusDto requestDto) {
    UserStatus userStatus = userIdMap.get(requestDto.id());

    if (requestDto.time() == null) {
      userStatus.lastAccessTimeUpdater();
    }

    userStatus.lastAccessTimeUpdater(requestDto.time());

    return userStatus;
  }

  @Override
  public boolean delete(RequestDeleteUserStatusDto requestDto) {
    userIdMap.remove(requestDto.id());
    idMap.remove(usernameMap.remove(requestDto.name()).getId());

    return true;
  }
}
