package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.CreateUserStatusDto;
import com.sprint.mission.discodeit.dto.DeleteUserStatusDto;
import com.sprint.mission.discodeit.dto.FindUserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("jcf")
public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> idMap = new ConcurrentHashMap<>();
    private final Map<UUID, UserStatus> userIdMap = new ConcurrentHashMap<>();
    private final Map<String, UserStatus> userNameMap = new ConcurrentHashMap<>();

    @Override
    public boolean create(CreateUserStatusDto requestDto){
        UserStatus userStatus = new UserStatus(requestDto.id(), requestDto.name());
        idMap.put(userStatus.getId(), userStatus);
        userIdMap.put(requestDto.id(), userStatus);
        userNameMap.put(requestDto.name(), userStatus);

        return true;
    }

    @Override
    public String find(FindUserStatusDto requestDto) {
        if(!userIdMap.containsKey(requestDto.id()))
            return "";
        return userIdMap.get(requestDto.id()).isOnline();
    }

    @Override
    public List<String> findAll(List<FindUserStatusDto> requestDto) {
        List<String> result = new ArrayList<>();
        requestDto.forEach(req -> result.add(find(req)));
        return result;
    }

    @Override
    public boolean update(UserStatusUpdateDto requestDto) {
        UserStatus userStatus = userIdMap.get(requestDto.id());

        if(requestDto.time() == null) {
            userStatus.lastAccessTimeUpdater();
        }

        userStatus.lastAccessTimeUpdater(requestDto.time());

        return true;
    }

    @Override
    public boolean delete(DeleteUserStatusDto requestDto) {
        userIdMap.remove(requestDto.id());
        idMap.remove(userNameMap.remove(requestDto.name()).getId());

        return true;
    }
}
