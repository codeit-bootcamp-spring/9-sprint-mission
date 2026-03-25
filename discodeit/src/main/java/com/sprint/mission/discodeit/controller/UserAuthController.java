//package com.sprint.mission.discodeit.controller;
//
//import com.sprint.mission.discodeit.controller.api.UserAuthApi;
//import com.sprint.mission.discodeit.dto.data.UserDto;
//import com.sprint.mission.discodeit.dto.request.LoginRequest;
//import com.sprint.mission.discodeit.service.AuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/auth")
//public class UserAuthController implements UserAuthApi {
//
//  private final AuthService authService;
//
//  @PostMapping(path = "/login")
//  @Override
//  public ResponseEntity<UserDto> login(@RequestBody LoginRequest loginRequest) {
//    try {
//      UserDto userDto = authService.login(loginRequest);
//      return ResponseEntity.ok(userDto);
//
//    } catch (Exception e) {
//      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }
//  }
//
//  @PostMapping(path = "/logout")
//  @Override
//  public ResponseEntity<Void> logout() {
//    return ResponseEntity
//        .status(HttpStatus.NO_CONTENT)
//        .build();
//  }
//}