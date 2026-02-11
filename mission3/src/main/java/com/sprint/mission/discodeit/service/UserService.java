package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.MyUserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(MyUserDto.AllInfo dto);
    MyUserDto.FindInfo find(UUID id);
    List<MyUserDto.FindInfo> findAll();
    User update(MyUserDto.UpdateInfo dto);
    void delete(UUID userId);
}
