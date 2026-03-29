package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> create(@Valid @RequestPart("userCreateRequest") UserCreateRequest createUserRequest,
                                            @RequestPart(value = "profile", required = false) MultipartFile imageFile)
        throws IOException {

        log.info("사용자 생성 요청 수신: username={}, email={}", createUserRequest.username(), createUserRequest.email());
        Optional<BinaryContentCreateRequest> binaryContentCreateRequest = Optional.empty();

        if (imageFile != null) {
            binaryContentCreateRequest = Optional.of(new BinaryContentCreateRequest(
                imageFile.getOriginalFilename(),
                imageFile.getContentType(),
                imageFile.getSize(),
                imageFile.getBytes()
            ));
        }

        UserDto newUserDto = userService.create(createUserRequest,binaryContentCreateRequest);
        log.info("사용자 생성 요청 처리 완료: username={}, email={}", createUserRequest.username(), createUserRequest.email());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newUserDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable UUID userId, @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request
        , @RequestPart(value = "profile", required = false) MultipartFile imageFile)
        throws IOException {

        Optional<BinaryContentCreateRequest> binaryContentCreateRequest = Optional.empty();

        if (imageFile != null) {
            binaryContentCreateRequest = Optional.of(new BinaryContentCreateRequest(
                imageFile.getOriginalFilename(),
                imageFile.getContentType(),
                imageFile.getSize(),
                imageFile.getBytes()
            ));
        }
        UserDto userDto = userService.update(userId, request, binaryContentCreateRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId){

        userService.delete(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findUserById(@PathVariable UUID userId){
        UserDto userDto = userService.find(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAll());
    }

    @PatchMapping(path = "/{userId}/userStatus")
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId(@PathVariable UUID userId,
        @Valid @RequestBody UserStatusUpdateRequest request) {
        UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId, request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedUserStatus);
    }

}
