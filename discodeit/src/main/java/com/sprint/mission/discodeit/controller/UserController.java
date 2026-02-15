package com.sprint.mission.discodeit.controller;
//package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController // 이 클래스가 REST API를 처리하는 컨트롤러임을 스프링에 전달
@RequiredArgsConstructor // final이 붙은 필드를 매개변수로 갖는 생성자를 자동으로 만들어줌
@RequestMapping("/api/users") // 이 컨트롤러 내의 모든 API주소를 공통 경로로 지정
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    //사용자 등록
    @RequestMapping(
            path = "/create",
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

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // 모든 사용자 조회
    @RequestMapping(
            path = "",
            method = RequestMethod.GET)
        public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll()); //ok 응답
    }

    // 사용자 정보 수정
    @RequestMapping(
            path = "/{userId}",
            method = RequestMethod.PUT,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<User> update(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);

        User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
        return ResponseEntity.ok(updatedUser);
    }



    // 사용자 삭제
    @RequestMapping(
            path = "/delete/{userId}",
            method = RequestMethod.DELETE) // 리소스를 삭제할 때 사용
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();// 삭제 성공 -> 204로 응답(반환할 데이터가 없음)
    }

//  온라인 업데이트
    @RequestMapping(
            path = "/{userId}/status",
            method = RequestMethod.PUT
    )
    public ResponseEntity<Boolean> updateUserStatus(
            @PathVariable UUID userId
    ) {
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());

        UserStatus updatedStatus = userStatusService.updateByUserId(userId,request);
        return Optional.ofNullable(updatedStatus)
                .map(UserStatus::isOnline)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(false));

}
    // 파일 변환 헬퍼 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
        if (profile == null || profile.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        } catch (IOException e) {
            throw new RuntimeException("파일 처리 실패", e);
        }
    }
}