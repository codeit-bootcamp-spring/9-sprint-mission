package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User API")
public interface UserApi {

  @Operation(summary = "Create user")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User created",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(responseCode = "400", description = "Duplicate email or username")
  })
  ResponseEntity<UserDto> create(
      @Parameter(
          description = "User create request",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) UserCreateRequest userCreateRequest,
      @Parameter(
          description = "User profile image",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ) MultipartFile profile
  );

  @Operation(summary = "Update user")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User updated",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "400", description = "Duplicate email or username")
  })
  ResponseEntity<UserDto> update(
      @Parameter(description = "User ID") UUID userId,
      @Parameter(description = "User update request") UserUpdateRequest userUpdateRequest,
      @Parameter(description = "User profile image") MultipartFile profile
  );

  @Operation(summary = "Delete user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User deleted"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "User ID") UUID userId
  );

  @Operation(summary = "Find all users")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Users found",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
      )
  })
  ResponseEntity<List<UserDto>> findAll();
}
