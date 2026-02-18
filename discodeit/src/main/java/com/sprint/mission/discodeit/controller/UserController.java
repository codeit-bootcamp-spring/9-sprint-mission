package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public ResponseEntity<User> creat(@RequestBody UserCreateRequest request) {
        User createdUser = userService.create(request, Optional.empty());
        return ResponseEntity.ok(createdUser);
    }

    //누구를 어떻게 수정할지
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public ResponseEntity<User> update(@PathVariable UUID id, @RequestBody UserUpdateRequest request) {
        User updatedUser = userService.update(id, request, Optional.empty());
        return ResponseEntity.ok(updatedUser);
    }
    //누구를 어떻게 삭제할지
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public  ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    //단건 조회
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> find(@PathVariable UUID id) {
        UserDto user = userService.find(id);
        return ResponseEntity.ok(user);
    }
    //전체 조회
    @RequestMapping(value = "/api/user/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll(){
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
    //온라인 여부만 바꿈
    @RequestMapping(value = "/users/{id}/online", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateOnlineStatus(@PathVariable UUID id){
        userStatusService.updateByUserId(id, new UserStatusUpdateRequest(Instant.now()));
        return ResponseEntity.noContent().build();
    }
}
