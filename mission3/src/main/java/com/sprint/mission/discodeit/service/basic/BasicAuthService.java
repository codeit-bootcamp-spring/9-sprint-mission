package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.LoginDto;
import com.sprint.mission.discodeit.DTO.MyUserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasicAuthService {
    private final UserRepository userrepository;

    public User login(LoginDto.LoginUser loginUser){
    return  userrepository.findAll()
            .stream()
            .filter(user->user.getUsername().equals(loginUser.username()))
            .filter(user->user.getPassword().equals(loginUser.password()))
            .findFirst()
            .orElseThrow(()->new RuntimeException("아이디 또는 비밀번호가 일치하지않습니다"));
    }
}
