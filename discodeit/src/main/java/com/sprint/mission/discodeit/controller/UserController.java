package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(
            path = "create",
            method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);

        User createdUser = userService.create(userCreateRequest, profileRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
        if (profile == null || profile.isEmpty()) return Optional.empty(); // null 체크 추가
        try {
            return Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        } catch (IOException e) {
            throw new RuntimeException("프로필 파일 처리 중 오류 발생", e);
        }
    }

    @RequestMapping(
            path = "/{userId}",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<User> update(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequest request
    ) {

        User updatedUser = userService.update(userId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    @RequestMapping(
            path = "/{userId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserStatusResponse>> findAll() {
        List<UserStatusResponse> users = userService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }









}