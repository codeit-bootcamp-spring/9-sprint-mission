package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //등록
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest request) {
        User user = userService.create(request, Optional.empty());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    //수정
    @RequestMapping(value = "/{userId}",method = RequestMethod.PATCH)
    public User updateUser(@RequestBody UserUpdateRequest request, @PathVariable UUID id) {
        UUID userId = UUID.fromString(id.toString());
        return userService.update(userId,request,Optional.empty());
    }

    //삭제
    @RequestMapping(value = "/{userId}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        UUID userId = UUID.fromString(id.toString());
        userService.delete(userId);
    }
    //조회
    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> findAll(){
        return userService.findAll();
    }
    //온라인 상태 업데이트
    @RequestMapping(value = "/{userId}/status",method = RequestMethod.PATCH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserStatus(@PathVariable UUID userId){
        UUID id = UUID.fromString(userId.toString());
        userService.updateOnlineStatus(userId);
    }
}
