package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.request.RequestChannelDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exepction.DoNotUpdatePrivateChannel;
import com.sprint.mission.discodeit.exepction.global.Forbidden;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
public class JCFChannelRepository implements ChannelRepository {

  private final Map<String, Channel> publicChannelNameMap = new ConcurrentHashMap<>();
  private final Map<UUID, Channel> publicChannelIdMap = new ConcurrentHashMap<>();
  private final Map<String, Channel> privateChannelNameMap = new ConcurrentHashMap<>();
  private final Map<UUID, Channel> privateChannelIdMap = new ConcurrentHashMap<>();

  /// interface
  @Override
  public ResponseChannelDto save(RequestChannelDto requestDto) {
    Channel channel = requestDto.toEntity();
    if (channel.getChannelType() == ChannelType.PUBLIC) {
      publicChannelNameMap.put(channel.getName(), channel);
      publicChannelIdMap.put(channel.getId(), channel);
    } else {
      privateChannelIdMap.put(channel.getId(), channel);
      privateChannelNameMap.put(channel.getName(), channel);
    }

    return toDto(channel);
  }

  @Override
  public ResponseChannelDto save(UUID channelId, RequestUpdateChannelDto requestDto) {
    String newName = requestDto.newName();
    String newDescription = requestDto.newDescription();
    if (!privateChannelIdMap.values().stream().filter(channel ->
        channel.getName().equals(requestDto.newName())
    ).toList().isEmpty()) {
      throw new DoNotUpdatePrivateChannel("Do not update private channel");
    }
    Channel channel = publicChannelIdMap.get(channelId);
    String oldName = channel.getName();
    publicChannelNameMap.put(newName, channel);
    publicChannelNameMap.remove(oldName);
    channel.channelUpdater(newName, newDescription);

    return toDto(channel);
  }

  @Override
  public ResponseChannelDto findChannel(UUID channelId, UUID userId) {
    if (publicChannelIdMap.containsKey(channelId)) {
      return toDto(publicChannelIdMap.get(channelId));
    }
    if (privateChannelIdMap.containsKey(channelId)) {
      if (privateChannelIdMap.get(channelId).getParticipantIds().containsValue(userId)) {
        return toDto(privateChannelIdMap.get(channelId));
      } else {
        throw new Forbidden("Cannot accessible this channel!");
      }
    }
    throw new NotFound("Channel not found");
  }

  @Override
  public ResponseChannelDto findChannel(UUID channelId) {
    if (publicChannelIdMap.containsKey(channelId)) {
      return toDto(publicChannelIdMap.get(channelId));
    }
    throw new NotFound("Channel not found");
  }

  @Override
  public ChannelType getChannelType(UUID id) {
    if (publicChannelIdMap.containsKey(id)) {
      return publicChannelIdMap.get(id).getChannelType();
    }
    if (privateChannelIdMap.containsKey(id)) {
      return privateChannelIdMap.get(id).getChannelType();
    }
    throw new NotFound("ChannelType not found");
  }

  @Override
  public UUID getChannelId(String name) {
    if (publicChannelNameMap.containsKey(name)) {
      return publicChannelNameMap.get(name).getId();
    }
    if (privateChannelNameMap.containsKey(name)) {
      return privateChannelNameMap.get(name).getId();
    }

    throw new NotFound("ChannelId not found");
  }

  @Override
  public List<ResponseChannelDto> findAllChannel(UUID userId) {
    List<ResponseChannelDto> result = new ArrayList<>();

    /// public
    result.addAll(publicChannelNameMap.values().stream().map(this::toDto).toList());

    /// private
    result.addAll(accessiblePrivateChannel(userId).stream().toList());
    return result;
  }

  @Override
  public List<ResponseChannelDto> findAllPrivateChannel(UUID userId) {
    if (accessiblePrivateChannel(userId).isEmpty()) {
      throw new NotFound("권한이 있는 Private Channel이 없습니다!");
    }

    return new ArrayList<>(accessiblePrivateChannel(userId).stream().toList());
  }

  @Override
  public List<ResponseChannelDto> accessiblePrivateChannel(UUID userId) {
    List<ResponseChannelDto> requestDto = new ArrayList<>();
    privateChannelIdMap.values().stream()
        .filter(channel -> channel.getChannelType().equals(ChannelType.PRIVATE))
        .filter(channel -> channel.getParticipantIds().containsValue(userId))
        .forEach(channel -> requestDto.add(toDto(channel)));

    return requestDto;
  }

  @Override
  public ResponseChannelDto toDto(Channel channel) {
    return new ResponseChannelDto(
        channel.getName(),
        channel.getDescription(),
        channel.getId(),
        channel.getChannelType(),
        channel.getCreateAt(),
        channel.getUpdateAt(),
        channel.getParticipantIds());
  }

  @Override
  public void includePrivateChannel(String channelName, String username, UUID userId) {
    privateChannelNameMap.get(channelName).addParticipantIds(username, userId);
  }

  @Override
  public void excludePrivateChannel(String channelName, String username) {
    privateChannelNameMap.get(channelName).removeParticipantIds(username);
  }

  @Override
  public boolean deleteChannel(UUID id) {
    String name;
    boolean isPrivate = privateChannelIdMap.containsKey(id);

    if (isPrivate) {
      name = privateChannelIdMap.get(id).getName();
    } else {
      name = publicChannelIdMap.get(id).getName();
    }

    if (isPrivate) {
      privateChannelNameMap.remove(name);
      privateChannelIdMap.remove(id);
    } else {
      publicChannelNameMap.remove(name);
      publicChannelIdMap.remove(id);
    }
    return true;
  }

  @Override
  public boolean isPresentChannel(UUID id) {
    return publicChannelIdMap.containsKey(id) || privateChannelIdMap.containsKey(id);
  }

  @Override
  public UUID channelNameToId(String name) {
    if (publicChannelNameMap.containsKey(name)) {
      return publicChannelNameMap.get(name).getId();
    }
    if (privateChannelNameMap.containsKey(name)) {
      return privateChannelNameMap.get(name).getId();
    }

    throw new NotFound("해당 채널을 찾을 수 없습니다");
  }

  @Override
  public String channelIdToName(UUID id) {
    if (publicChannelIdMap.containsKey(id)) {
      return publicChannelIdMap.get(id).getName();
    }
    if (privateChannelIdMap.containsKey(id)) {
      return privateChannelIdMap.get(id).getName();
    }

    throw new NotFound("해당 채널을 찾을 수 없습니다");
  }
}
