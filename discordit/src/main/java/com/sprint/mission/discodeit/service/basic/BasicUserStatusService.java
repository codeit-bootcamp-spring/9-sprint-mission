package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserStatusDto;
import com.sprint.mission.discodeit.dto.DeleteUserStatusDto;
import com.sprint.mission.discodeit.dto.FindUserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.exepction.DoNotDuplicate;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public void create(CreateUserStatusDto requestDto){
        FindUserStatusDto dto = new FindUserStatusDto(requestDto.id(), requestDto.name());

        if(userRepository.userIdToName(requestDto.id()).isEmpty())
            throw new NotFound("해당 유저가 없습니다.");
        if(!userStatusRepository.find(dto).isEmpty())
            throw new DoNotDuplicate("이미 존재합니다.");

        userStatusRepository.create(requestDto);
    }
    public String find(FindUserStatusDto requestDto) {
        return userStatusRepository.find(requestDto);
    }
    public void findAll() {}
    public void update(UserStatusUpdateDto requestDto) {
        userStatusRepository.update(requestDto);
    }
    public void delete(DeleteUserStatusDto requestDto) {
        userStatusRepository.delete(requestDto);
    }
}
