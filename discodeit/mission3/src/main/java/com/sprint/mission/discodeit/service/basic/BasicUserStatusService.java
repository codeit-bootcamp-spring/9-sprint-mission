package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusDto.createUserStatus createUserStatus) {
        User user = userRepository.findById(createUserStatus.userId())
                .orElseThrow(()->new NoSuchElementException("유저가 없습니다"));
        userStatusRepository.findByUserId(createUserStatus.userId())
                .ifPresent(s->{throw new IllegalArgumentException("이미 객체 상태가 존재함");});
        UserStatus userStatus = new UserStatus(user);
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus findById(UUID id) {
       return userStatusRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("유저의 상태 없음"));

    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UserStatusDto.updateUserStatus userStatus) {
       UserStatus status = userStatusRepository.findByUserId(userStatus.userId())
               .orElseThrow(()->new NoSuchElementException("유저의 정보가 존재하지않습니다."));
       if(userStatus.isOnline()){
           status.updateLastedAt();
       }else{
           status.forceOnline();
       }
       return userStatusRepository.save(status);
    }

    @Override
    public UserStatus updateByUserId(UUID userid) {
        UserStatus status = userStatusRepository.findByUserId(userid)
                .orElseThrow(()->new NoSuchElementException("유저가 없어요"));
        status.updateLastedAt();
        return userStatusRepository.save(status);

    }

    @Override
    public boolean delete(UUID id) {
        if(!userStatusRepository.existUserId(id)){
            return false;
        }
        userStatusRepository.deleteStatus(id);
        return true;
    }
}
