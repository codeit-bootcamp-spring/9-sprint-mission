package com.sprint.mission.discodeit.service.status;

import com.sprint.mission.discodeit.DTO.UserStatusDTO;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;


    public UserStatus createUserStatus(UserStatusDTO.CreateUserStatusDTO dto){
        if (!userRepository.existsById(dto.userId())){
            throw new NoSuchElementException("can't find user with id " + dto.userId());
        }
        if (userRepository.equals(dto.userId())){
            throw new IllegalArgumentException("you can't mark yourself as online");
        }

        UserStatus userStatus = new UserStatus(
                dto.userId()
        );
        userStatusRepository.save(userStatus);
        return userStatus;
    }

    public UserStatus findByUserId(UUID userId){
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("user with id " + userId + "not found"));
    }

    public UserStatus findAllById(UUID userId){
        return userStatusRepository.findAllByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("user with id " + userId + "not found"));
    }


    public UserStatus updateUserStatus(UserStatusDTO.UpdateUserStatusDTO dto){
        if (!userStatusRepository.existsById(dto.userId())){
            throw new NoSuchElementException("can't find user with id " + dto.userId());
        }
        if (userStatusRepository.equals(dto.userId())){
            return new UserStatus(dto.userId());
        }
        return null;
    }

    public UserStatus updateByUserId(UUID userId, UserStatus userStatus){
        if (!userRepository.existsById(userId)){
            throw new NoSuchElementException("can't find user with id " + userId);
        }
        if (userRepository.equals(userId)){
            userStatusRepository.updateByUserId(userId, userStatus);
        }
        return null;
    }

    public void deleteUserStatus(UUID userId){
        userStatusRepository.deleteById(userId);
    }





}


