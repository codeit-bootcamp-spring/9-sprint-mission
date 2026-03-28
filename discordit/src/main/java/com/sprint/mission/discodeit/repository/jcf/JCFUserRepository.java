package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserDto;
import com.sprint.mission.discodeit.dto.response.ResponseUserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exepction.DoNotDuplicate;
import com.sprint.mission.discodeit.exepction.FailedDelete;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.ArrayList;
import java.util.Comparator;
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
public class JCFUserRepository implements UserRepository {

  private final Map<UUID, User> idUserMap = new ConcurrentHashMap<>();
  private final Map<String, UUID> usernameIdMap = new ConcurrentHashMap<>();

  @Override
  public ResponseUserDto create(RequestCreateUserDto dto, UUID profileId) {
    User user = dto.toEntity(profileId);
    usernameIdMap.put(user.getName(), user.getId());
    idUserMap.put(user.getId(), user);

    return response(user);
  }

  @Override
  public ResponseUserDto update(UUID userId, RequestUpdateUserDto requestDto, UUID profileId) {
    String reName = requestDto.newUsername();
    String rePassword = requestDto.newPassword();
    String reMail = requestDto.newEmail();
    User user = idUserMap.get(userId);

    user.updateUser(reName, rePassword, reMail, profileId);
    return response(user);
  }

  @Override
  public ResponseUserDto find(String name) {
    User user = idUserMap.get(usernameIdMap.get(name));
    return response(user);
  }

  @Override
  public ResponseUserDto find(UUID userId) {
    User user = idUserMap.getOrDefault(userId, null);
    if (user == null) {
      throw new NotFound("Not Found This User Id");
    }

    return response(user);
  }

  @Override
  public List<ResponseUserDto> findAll() {
    List<ResponseUserDto> result = new ArrayList<>();
    idUserMap.values().stream().sorted(Comparator.comparing(User::getName))
        .forEach(user -> result.add(response(user)));
    return result;
  }

  private ResponseUserDto response(User user) {
    return new ResponseUserDto(user.getId(), user.getCreateAt(), user.getUpdateAt(), user.getName(),
        user.getEmail(), user.getProfileId());
  }

  @Override
  public boolean delete(UUID id) {
    String name = idUserMap.get(id).getName();
    try {
      usernameIdMap.remove(idUserMap.get(id).getName());
      idUserMap.remove(id);
    } catch (Exception e) {
      throw new FailedDelete("Delete is failed : " + name);
    }
    return true;
  }

  @Override
  public boolean isPresent(UUID id) {
    return idUserMap.containsKey(id);
  }

  @Override
  public UUID usernameToId(String name) {
    try {
      return usernameIdMap.get(name);
    } catch (Exception e) {
      throw new NotFound("Do not found this user : " + name);
    }
  }

  @Override
  public String userIdToName(UUID id) {
    try {
      return idUserMap.get(id).getName();
    } catch (Exception e) {
      throw new NotFound("Do not found this user : " + id);
    }
  }

  @Override
  public boolean checkInvalid(UUID id, String pw) {
    try {
      return !idUserMap.get(id).getPassword().equals(pw);
    } catch (Exception e) {
      return true;
    }
  }

  @Override
  public void duplicateChecker(String checkThis, String findThis) {
    if (findThis == null || findThis.isEmpty()) {
      return;
    }

    switch (checkThis) {
      case "이메일":
        if (idUserMap.values().stream().anyMatch(u ->
            findThis.equals(u.getEmail()))) {
          throw new DoNotDuplicate("This email already exists: " + findThis);
        }
        break;
      case "전화번호":
        if (idUserMap.values().stream().anyMatch(u ->
            findThis.equals(u.getPhoneNumber()))) {
          throw new DoNotDuplicate("This phoneNumber already exists: " + findThis);
        }
        break;
      case "사용자명":
        if (usernameIdMap.containsKey(findThis)) {
          throw new DoNotDuplicate("This username already exists: " + findThis);
        }
        break;
    }
  }
}
