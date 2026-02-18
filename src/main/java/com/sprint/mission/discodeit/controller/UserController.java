package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final ChannelService channelService;


    @RequestMapping(method = RequestMethod.POST)
    public UserDto createUser(@RequestBody UserCreateRequest request) {
        User created = userService.create(request, Optional.empty());
        return userService.find(created.getId());
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID userId) {
        userService.delete(userId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    public UserStatus updateStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        return userStatusService.updateByUserId(userId, request);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public UserDto find(@PathVariable UUID userId) {
        return userService.find(userId);
    }

    @RequestMapping(value = "/{userId}/channels", method = RequestMethod.GET)
    public List<ChannelDto> findVisibleChannels(@PathVariable UUID userId) {
        return channelService.findAllByUserId(userId);
    }


}
