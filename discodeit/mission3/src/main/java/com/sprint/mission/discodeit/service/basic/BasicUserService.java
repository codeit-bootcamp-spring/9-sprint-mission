package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.MyUserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public User create(MyUserDto.AllInfo dto) {
        if(userRepository.existsByName(dto.basicInfo().username())){
            throw new IllegalArgumentException("회원이 존재함.");
        }
        if(userRepository.existsByEmail(dto.basicInfo().email())){
            throw new IllegalArgumentException("이메일이 존재함");
        }
        User user = new User(dto.basicInfo().username(),dto.basicInfo().email(),dto.basicInfo().password());
        userRepository.save(user);
        UserStatus userStatus =new UserStatus(user);
        userStatusRepository.save(userStatus);

        if(dto.profileInfo()!=null){
            UUID profileId = dto.profileInfo().profileId();
            if(!binaryContentRepository.existById(profileId)){
                throw new NoSuchElementException("존재하지 않는 ID입니다.");
            }
            user.updateProfile(profileId);
            userRepository.save(user);
        }
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
                .map(user ->
                {UserStatus status = userStatusRepository.findByUserId(user.getId()).orElseGet(()->new UserStatus(user));
                return new MyUserDto.FindInfo(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        status.isOnline()
                );
                }).toList();


    }

    @Override
    public User update(MyUserDto.UpdateInfo dto) {
        User user = userRepository.findById(dto.userid())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
       if(!dto.newEmail().equals(user.getEmail())){
           if(userRepository.existsByEmail(dto.newEmail())){
               throw new IllegalArgumentException("이미 사용중인 이메일");
           }
       }
        user.update(dto.newName(),dto.newEmail(), dto.password());
        return userRepository.save(user);


    }

    @Override
    public void delete(UUID userId) {
       User user = userRepository.findById(userId)
                       .orElseThrow(()->new NoSuchElementException("유저가 없습니다!"));

        if(user.getProfileId()!=null){
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userStatusRepository.deleteStatus(userId);
        userRepository.deleteById(userId);
    }
}
