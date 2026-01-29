package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.MyUserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(MyUserDto.BasicInfo dto);
    MyUserDto.FindInfo find(UUID id);
    List<MyUserDto.FindInfo> findAll();
    User update(UUID userId, String newUsername, String newEmail, String newPassword);
    void delete(UUID userId);
}
