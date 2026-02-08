package com.sprint.mission.discodeit.service.Auth;

import com.sprint.mission.discodeit.DTO.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AuthService {
   private final UserRepository userRepository;


    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(UserDTO.loginDTO dto){
        User user = userRepository.findByUserName(dto.userName())
                .orElseThrow(() ->
                        new NoSuchElementException("로그인 정보를 다시 확인해주세요")
                );

        if (!user.getPassword().equals(dto.password())) {
            throw new NoSuchElementException("로그인 정보를 다시 확인해주세요");
        }

        return user;

    }






}
