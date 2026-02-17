package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;


    @RequestMapping(
            path="/create",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            method =RequestMethod.POST
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
            path="/find",
            method = RequestMethod.GET
    )
    public ResponseEntity<UserDto> find(
            @RequestParam("id") UUID userid
    ){
        UserDto findUser = userService.find(userid);
        return ResponseEntity.ok(findUser);
    }

    @RequestMapping(
            path= "/findAll",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<UserDto>> findAll(){
        List<UserDto> userList = userService.findAll();
        return ResponseEntity.ok(userList);

    }
    @RequestMapping(
            path ="update",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            method = RequestMethod.PUT

    )
    public ResponseEntity<User> update(
            @RequestParam UUID userId,
            @RequestPart ("UserUpdateRequest")UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile

    ){
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile).flatMap(this::resolveProfileRequest);
        User updateUser= userService.update(userId,userUpdateRequest,profileRequest);
        return ResponseEntity.ok(updateUser);


    }
    @RequestMapping(
            path="delete",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @RequestParam UUID userId
    ){
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            path = "/UserStatus", method = RequestMethod.PUT
    )
    public ResponseEntity<UserStatus> StatusUpdateByUserId(
            @RequestParam UUID userId,
            @RequestBody UserStatusUpdateRequest Request
            ){
        UserStatus updateUser = userStatusService.updateByUserId(userId,Request);
        return ResponseEntity.ok(updateUser);
    }







    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile){
        if(profile ==null || profile.isEmpty()){
            return Optional.empty();
        }
        try{
            return Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }catch (IOException e){
            throw new RuntimeException("파일 생성중 오류 발생",e);
        }


    }

}
