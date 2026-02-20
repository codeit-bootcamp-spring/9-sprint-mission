package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.userStatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // 테스트 때문에 허용
public class UserController implements UserApi {
    private final UserService userService;
    private final BinaryContentService binaryContentService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(@RequestPart("userInfo") CreateUserRequest createUserRequest,
                                            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        UUID profileImageId = null;

        if (!imageFile.isEmpty()) {
            BinaryContent binaryContent = binaryContentService.uploadFile(imageFile);

            profileImageId = binaryContent.getId();
        }

        User newUser = userService.create(createUserRequest, profileImageId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> update(@PathVariable UUID userId, @RequestPart("userInfo") UpdateUserRequest request
        , @RequestPart(value = "imageFile", required = false) MultipartFile imageFile){
        UUID profileImageId = null;

        if (!imageFile.isEmpty()) {
            BinaryContent binaryContent = binaryContentService.uploadFile(imageFile);

            profileImageId = binaryContent.getId();
        }
        User user = userService.update(userId, request, profileImageId );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId){

        userService.remove(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable UUID userId){
        UserResponse userResponse = userService.findByID(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userResponse);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAll());
    }

    @PatchMapping(path = "/{userId}/userStatus")
    public ResponseEntity<UserStatus> updateUserStatusByUserId(@PathVariable UUID userId,
        @RequestBody UpdateUserStatusRequest request) {
        UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedUserStatus);
    }

}
