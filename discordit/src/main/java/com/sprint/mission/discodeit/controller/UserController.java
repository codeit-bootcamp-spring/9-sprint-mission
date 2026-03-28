package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.RequestCreateBinaryContentDto;
import com.sprint.mission.discodeit.dto.request.RequestCreateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestCreateUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestFindUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserStatusDto;
import com.sprint.mission.discodeit.dto.response.ResponseUserDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final BasicBinaryContentService binaryContentService;
  private final UserService userService;
  private final UserStatusService userStatusService;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseUserDto handleCreateUser(
      @RequestPart("userCreateRequest") RequestCreateUserDto createUserDto,
      @RequestParam(value = "profile", required = false) MultipartFile file
  ) throws IOException {
    RequestCreateBinaryContentDto binaryContentCreateRequestDto;

    UUID profileId = null;
    if (file != null && !file.isEmpty()) {
      binaryContentCreateRequestDto = new RequestCreateBinaryContentDto(file.getContentType(),
          file.getName(), file.getBytes());
      profileId = binaryContentService.create(binaryContentCreateRequestDto);
    }

    ResponseUserDto responseDto = userService.create(createUserDto, profileId);

    UUID userId = responseDto.id();

    userStatusService.create(new RequestCreateUserStatusDto(createUserDto.username(), userId));

    return responseDto;
  }

  @ResponseBody
  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public UserDto handleFindUserById(@PathVariable UUID id) {
    ResponseUserDto responseUserDto = userService.find(id);
    Boolean isOnline = userStatusService.find(
        new RequestFindUserStatusDto(responseUserDto.id(), responseUserDto.username()));
    return UserDto.from(responseUserDto, isOnline);
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.GET)
  public List<UserDto> handleFindAllUser() {
    List<ResponseUserDto> responseUserDtos = userService.findAll();
    List<UserDto> result = new ArrayList<>();

    for (ResponseUserDto responseUserDto : responseUserDtos) {
      Boolean isOnline = userStatusService.find(
          new RequestFindUserStatusDto(responseUserDto.id(), responseUserDto.username()));
      result.add(UserDto.from(responseUserDto, isOnline));
    }

    return result;
  }

  @RequestMapping(value = "{id}", method = RequestMethod.PATCH)
  public ResponseUserDto handleUpdateUser(
      @PathVariable UUID id,
      @RequestPart(value = "userUpdateRequest") RequestUpdateUserDto updateUserDto,
      @RequestParam(value = "profile", required = false) MultipartFile file
  ) throws IOException {
    RequestCreateBinaryContentDto binaryContentCreateRequestDto;

    UUID profileId = null;
    if (file != null && !file.isEmpty()) {
      binaryContentCreateRequestDto = new RequestCreateBinaryContentDto(file.getContentType(),
          file.getName(), file.getBytes());
      profileId = binaryContentService.create(binaryContentCreateRequestDto);
    }

    return userService.update(id, updateUserDto, profileId);
  }

  @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
  public ResponseEntity<String> handleDeleteUser(
      @PathVariable UUID id
  ) {
    String username = userService.find(id).username();
    userService.delete(id);
    return new ResponseEntity<>("Success deleted! : " + username, HttpStatus.OK);
  }

  @RequestMapping(value = "{id}/userStatus", method = RequestMethod.PATCH)
  public UserStatus handleUserStatus(
      @PathVariable UUID id,
      @RequestParam(value = "newLastActiveAt", required = false) Instant newLastActiveAt
  ) {
    String username = userService.find(id).username();

    RequestUpdateUserStatusDto updateRequestDto = new RequestUpdateUserStatusDto(id, username,
        newLastActiveAt);

    return userStatusService.update(updateRequestDto);
  }
}