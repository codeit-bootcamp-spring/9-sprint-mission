package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> createUser(
        @RequestPart("userCreateRequest") UserCreateRequest request,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userService.create(request, profile));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> updateUser(
        @PathVariable UUID userId,
        @RequestPart("userUpdateRequest") UserUpdateRequest request,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        return ResponseEntity.ok(userService.update(userId, request, profile));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}