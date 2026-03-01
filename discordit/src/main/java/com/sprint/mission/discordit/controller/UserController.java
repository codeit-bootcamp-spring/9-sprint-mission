package com.sprint.mission.discordit.controller;

import com.sprint.mission.discordit.dto.data.UserDto;
import com.sprint.mission.discordit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discordit.dto.request.UserCreateRequest;
import com.sprint.mission.discordit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discordit.dto.request.UserUpdateRequest;
import com.sprint.mission.discordit.entity.UserStatus;
import com.sprint.mission.discordit.global.ApiResult;
import com.sprint.mission.discordit.service.UserService;
import com.sprint.mission.discordit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Operation(summary = "사용자 생성", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(mediaType = "multipart/form-data",
          encoding = @Encoding(name = "userCreateRequest", contentType = "application/json")
      )
  ))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "사용자 생성 완료"),
      @ApiResponse(responseCode = "400", description = "사용자명 혹은 이메일 중복", content = @Content)
  })
  @RequestMapping(
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
      method = RequestMethod.POST
  )
  public ResponseEntity<UserDto> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdUser);
  }

  @Operation(summary = "사용자 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "사용자 수정 완료"),
      @ApiResponse(responseCode = "400", description = "사용자명 혹은 이메일 중복", content = @Content)
  })
  @RequestMapping(
      path = "{userId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
      method = RequestMethod.PATCH
  )
  public ResponseEntity<ApiResult<UserDto>> update(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResult.success(updatedUser));
  }

  @Operation(summary = "사용자 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "사용자 삭제 완료"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자", content = @Content)
  })
  @RequestMapping(path = "{userId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "사용자 전체 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "사용자 전체 조회 완료")
  })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  @Operation(summary = "사용자 상태 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "사용자 수정 완료"),
      @ApiResponse(responseCode = "400", description = "사용자 상태 조회 불가", content = @Content)
  })
  @RequestMapping(path = "{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatusByUserId(@PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request) {
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUserStatus);
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profileFile.getOriginalFilename(),
            profileFile.getContentType(),
            profileFile.getBytes()
        );
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
