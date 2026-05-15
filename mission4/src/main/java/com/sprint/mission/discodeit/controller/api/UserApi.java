package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;

import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User API")
public interface UserApi {

  @Operation(summary = "User 등록", operationId = "create")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "User가 성공적으로 생성되었습니다.",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 username 또는 email이 존재합니다.",
          content = @Content(examples = @ExampleObject(value = "User with email {email} already exists"))
      ),
  })
  public ResponseEntity<UserDto> create(
      @Parameter(description = "User 생성 정보", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) @RequestPart UserCreateRequest userCreateRequest,
      @Parameter(description = "User프로필 이미지", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) @RequestPart MultipartFile profile);

  //  public ResponseEntity<UserDto> find(UUID userId);
  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(responseCode = "200", description = "User 목록 조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
  public ResponseEntity<List<UserDto>> findAll();

  @Operation(summary = "User 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(examples = @ExampleObject(value = "user with email {newEmail} already exists"))
      ),
      @ApiResponse(
          responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {userId} not found"))
      )
  })
  public ResponseEntity<UserDto> update(
      @Parameter(description = "수정할 User ID") @PathVariable UUID userId,
      @Parameter(description = "수정할 User 정보") @RequestPart UserUpdateRequest userUpdateRequest,
      @Parameter(description = "수정할 User 프로필 정보") @RequestPart MultipartFile profile);

  @Operation(summary = "User 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {id} not found")))
  })
  public ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 User ID", required = true) @PathVariable UUID userId);


}
