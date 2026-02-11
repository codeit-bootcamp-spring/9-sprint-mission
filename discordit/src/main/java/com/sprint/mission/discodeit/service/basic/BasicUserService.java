package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.dto.UpdateUserDto;
import com.sprint.mission.discodeit.dto.UserFinder;
import com.sprint.mission.discodeit.exepction.FailedFound;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UUID nullUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public UUID userNameToId(String name) {
        return userRepository.userNameToId(name);
    }

    @Override
    public boolean isPresent(String name) {
        return userRepository.userNameToId(name) != null;
    }

    @Override
    public boolean isValid(UUID userId, String password) {
        return userRepository.checkInvalid(userId, password);
    }

    @Override
    public boolean create(CreateUserDto requestDto) {
        UUID userId = userRepository.create(requestDto);

        return userId != nullUUID;
    }

    /// Update
    @Override
    public boolean update(UpdateUserDto requestDto) {
        UUID reProfileId = requestDto.reProfileId();
        UUID userId = requestDto.id();

        if(userRepository.update(requestDto)) {
            if(reProfileId != null){
                binaryContentRepository.delete(userId);
            }
            return true;
        }
        return false;
    }

    /// Read
    @Override
    public UserFinder find(String name) {
        if(userRepository.userNameToId(name) == null)
            throw new FailedFound("해당 사용자를 찾지 못했습니다");
        return userRepository.find(name);
    }

    @Override
    public List<UserFinder> findAll() {
        return userRepository.findAll();
    }

    /// Delete
    @Override
    public boolean delete(UUID id) {
        binaryContentRepository.delete(id);
        return userRepository.delete(id);
    }
}
