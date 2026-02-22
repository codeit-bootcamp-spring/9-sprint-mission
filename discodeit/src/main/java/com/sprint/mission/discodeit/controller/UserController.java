package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDeleteRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserView;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @RequestMapping(method = RequestMethod.POST)
  public UserView create(@RequestBody UserCreateRequest request) {
    return userService.create(request);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserView> users = userService.findAll();
    List<UserDto> result = users.stream()
        .map(this::toDto)
        .toList();
    return ResponseEntity.ok(result);
  }

  @RequestMapping(value = "/api/user/findAll", method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAllAlias() {
    return findAll();
  }

  @RequestMapping(method = RequestMethod.GET, params = "username")
  public UserView findByUsername(@RequestParam("username") String username) {
    return userService.findByUsername(username);
  }

  private UserDto toDto(UserView v) {
    return new UserDto(
        v.id(),
        v.createdAt(),
        v.updatedAt(),
        v.username(),
        v.email(),
        v.profileImageId(),
        v.online()
    );
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
  public UserView findById(@PathVariable UUID userId) {
    return userService.findById(userId);
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
  public UserView update(
      @PathVariable UUID userId,
      @RequestBody UserUpdateRequest.Params params
  ) {
    return userService.update(new UserUpdateRequest(userId, params));
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
  public void delete(@PathVariable UUID userId) {
    userService.delete(new UserDeleteRequest(userId));
  }

}