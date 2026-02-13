package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ResponseBody
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(
            path = "create",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile).flatMap(this::resolveProfileRequest);
        User createdUser = userService.create(userCreateRequest, profileRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @RequestMapping(
            path = "update",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<User> update(
            @RequestParam("userId") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ){
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile).flatMap(this::resolveProfileRequest);
        User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    @RequestMapping(path = "delete")
    public ResponseEntity<User> delete(
            @RequestParam("userId") UUID userId
    ){
        userService.delete(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @RequestMapping(path = "find")
    public ResponseEntity<UserDto> find(
            @RequestParam("userId") UUID userId
    ){
        UserDto user = userService.find(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @RequestMapping(path = "findAll")
    public ResponseEntity<List<UserDto>> findAll(){
        List<UserDto> users =  userService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @RequestMapping(path = "online")
    public ResponseEntity<UserStatus> isOnline(
            @RequestParam("userId") UUID userId,
            @RequestPart("UserStatusUpdateRequest") UserStatusUpdateRequest userStatusUpdateRequest){
        UserStatus updatedUserStatus
                = userStatusService.updateByUserId(userId,userStatusUpdateRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUserStatus);
        }


    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
       if (profileFile == null || profileFile.isEmpty()) {
       return Optional.empty();
       }
       try {
           BinaryContentCreateRequest binaryContentCreateRequest
                   = new BinaryContentCreateRequest(
                   profileFile.getOriginalFilename(),
                   profileFile.getContentType(),
                   profileFile.getBytes()
           );
           return Optional.of(binaryContentCreateRequest);

       }catch (IOException e) {
           throw new RuntimeException(e);
       }

    }




}
