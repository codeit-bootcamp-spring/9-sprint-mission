package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDeleteRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserView;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public UserView create(@RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public List<UserView> findAll() {
        return userService.findAll();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public UserView findById(@PathVariable("userId") UUID userId) {
        return userService.findById(userId);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    public UserView update(
            @PathVariable("userId") UUID userId,
            @RequestBody UserUpdateRequest.Params params
    ) {
        return userService.update(new UserUpdateRequest(userId, params));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("userId") UUID userId) {
        userService.delete(new UserDeleteRequest(userId));
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public UserView findByUsername(@RequestParam("username") String username) {
        return userService.findByUsername(username);
    }
}