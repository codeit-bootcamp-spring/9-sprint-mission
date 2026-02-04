package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.MyUserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;


    @Override
    public User create(MyUserDto.BasicInfo dto) {
        if(userRepository.existsByName(dto.username())){
            throw new IllegalArgumentException("회원이 존재함.");
        }
        if(userRepository.existsByEmail(dto.email())){
            throw new IllegalArgumentException("이메일이 존재함");
        }
        User user = new User(dto);
        UserStatus userStatus =new UserStatus(user);
         userRepository.save(user);
         userStatusRepository.save(userStatus);
         return user;

    }

    @Override
    public MyUserDto.FindInfo find(UUID id) {
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("회원을 찾을수가없음"));
        UserStatus status = userStatusRepository.findByUserId(id).orElseThrow(()->new RuntimeException("상태정보가없음"));
        return new MyUserDto.FindInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                status.isOnline()
        );
    }

    @Override
    public List<MyUserDto.FindInfo> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> this.find(user.getId()))
                .toList();
    }

    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        user.update(newUsername, newEmail, newPassword);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        if(!userStatusRepository.existUserId(userId)){
            throw new NoSuchElementException("User with staus" + userId + "not found");
        }
        userStatusRepository.deleteStatus(userId);
        userRepository.deleteById(userId);
    }
}
