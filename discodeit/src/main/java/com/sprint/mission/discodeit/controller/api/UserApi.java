package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "User", description = "User API")
public interface UserApi {

  @Operation(summary = "User 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "User가 성공적으로 생성됨",
          // 🌟 수정 1: 응답 스키마를 User.class에서 UserDto.class로 변경!
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(examples = @ExampleObject(value = "User with email {email} already exists"))
      ),
  })
    // 🌟 수정 2: 반환 타입을 ResponseEntity<UserDto>로 변경!
  ResponseEntity<UserDto> create(
      @Parameter(
          description = "User 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) UserCreateRequest userCreateRequest,
      @Parameter(
          description = "User 프로필 이미지",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ) MultipartFile profile
  );

  @Operation(summary = "User 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 정보가 성공적으로 수정됨",
          // 🌟 수정 3: 여기도 UserDto.class로 변경!
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      // ... (에러 응답 생략, 기존과 동일) ...
  })
    // 🌟 수정 4: 반환 타입을 ResponseEntity<UserDto>로 변경!
  ResponseEntity<UserDto> update(
      @Parameter(description = "수정할 User ID") UUID userId,
      @Parameter(description = "수정할 User 정보") UserUpdateRequest userUpdateRequest,
      @Parameter(description = "수정할 User 프로필 이미지") MultipartFile profile
  );

  @Operation(summary = "User 삭제")
    // ... (삭제는 반환값이 Void라 고칠 게 없어서 기존과 동일하게 유지합니다) ...
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 User ID") UUID userId
  );

  @Operation(summary = "전체 User 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 목록 조회 성공",
          // 🌟 수정 5: List가 아니라 PageResponse 형태라는 것을 명시!
          content = @Content(schema = @Schema(implementation = PageResponse.class))
      )
  })
    // 🌟 수정 6: List<UserDto> -> PageResponse<UserDto>로 바꾸고, 컨트롤러와 짝을 맞추기 위해 Pageable 매개변수 추가!
  ResponseEntity<PageResponse<UserDto>> findAll(
      @Parameter(hidden = true) Pageable pageable
  );

  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
          // 🌟 수정 7: UserStatus.class -> UserStatusDto.class로 변경!
          content = @Content(schema = @Schema(implementation = UserStatusDto.class))
      ),
      // ... (에러 응답 생략) ...
  })
    // 🌟 수정 8: 반환 타입을 ResponseEntity<UserStatusDto>로 변경!
  ResponseEntity<UserStatusDto> updateUserStatusByUserId(
      @Parameter(description = "상태를 변경할 User ID") UUID userId,
      @Parameter(description = "변경할 User 온라인 상태 정보") UserStatusUpdateRequest request
  );
}