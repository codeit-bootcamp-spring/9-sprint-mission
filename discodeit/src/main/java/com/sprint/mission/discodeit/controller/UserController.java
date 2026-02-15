package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // 테스트 때문에 허용
public class UserController {
    private final UserService userService;
    private final BinaryContentService binaryContentService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create( @RequestPart("userInfo") CreateUserRequest createUserRequest,
                                            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        UUID profileImageId = null;

        if (!imageFile.isEmpty()) {
            BinaryContent binaryContent = binaryContentService.uploadFile(imageFile);

            profileImageId = binaryContent.getId();
        }

        User newUser = userService.create(createUserRequest, profileImageId);
        return ResponseEntity.ok(newUser);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<User> update(@RequestBody UpdateUserRequest request){
        User user = userService.update(request);
        return ResponseEntity.ok(user);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteUser(@RequestParam UUID userId){
       userService.remove(userId);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> findUserById(@PathVariable UUID userId){
        UserResponse userResponse = userService.findByID(userId);
        return ResponseEntity.ok(userResponse);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> findUserAll(){
        return ResponseEntity.ok(userService.findAll());
    }
}
