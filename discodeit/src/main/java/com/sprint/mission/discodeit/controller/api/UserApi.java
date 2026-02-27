package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.api.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserView;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "User API")
@RequestMapping("/api/users")
public interface UserApi {

  @Operation(summary = "User 등록")
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "User가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = UserView.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<UserView> create(
      @Parameter(description = "User 생성 정보(JSON Part)")
      @RequestPart("userCreateRequest") UserCreateRequest request,
      @Parameter(
          description = "User 프로필 이미지(옵션)",
          content = @Content(
              mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
              schema = @Schema(type = "string", format = "binary")
          )
      )
      @RequestPart(value = "profile", required = false) MultipartFile profile
  );

  @Operation(summary = "User 목록 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "조회 성공",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserDto.class, type = "array")
          )
      )
  })
  @GetMapping
  ResponseEntity<List<UserDto>> findAll();

  @Operation(summary = "username으로 User 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "조회 성공",
          content = @Content(schema = @Schema(implementation = UserView.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @GetMapping(params = "username")
  UserView findByUsername(
      @Parameter(description = "username")
      @RequestParam("username") String username
  );

  @Operation(summary = "User 단건 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "조회 성공",
          content = @Content(schema = @Schema(implementation = UserView.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @GetMapping("/{userId}")
  UserView findById(
      @Parameter(description = "User ID")
      @PathVariable UUID userId
  );

  @Operation(summary = "User 정보 수정")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "수정 성공",
          content = @Content(schema = @Schema(implementation = UserView.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<UserView> update(
      @Parameter(description = "User ID")
      @PathVariable UUID userId,
      @Parameter(description = "User 수정 정보(JSON Part)")
      @RequestPart("userUpdateRequest") UserUpdateRequest.Params params,
      @Parameter(
          description = "User 프로필 이미지(옵션)",
          content = @Content(
              mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
              schema = @Schema(type = "string", format = "binary")
          )
      )
      @RequestPart(value = "profile", required = false) MultipartFile profile
  );

  @Operation(summary = "User 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @DeleteMapping("/{userId}")
  ResponseEntity<Void> delete(
      @Parameter(description = "User ID")
      @PathVariable UUID userId
  );
}
