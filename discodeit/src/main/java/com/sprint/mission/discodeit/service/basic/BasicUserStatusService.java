package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public String create(UserStatusCreateRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new RuntimeException("해당 유저를 찾을 수 없습니다.");
        }
        if (userStatusRepository.existsByUserId(request.userId())) {
            throw new RuntimeException("이미 상태 정보가 존재하는 유저입니다.");
        }
        UserStatus userStatus = new UserStatus(request.userId(), request.type());
        UserStatus savedStatus = userStatusRepository.save(userStatus);

        return savedStatus.getId().toString();
    }
/* 매개변수로 UserStatusCreateRequest를 받아서 만약에 request.userId가 userRepository.existsById 메서드로 못찾았을때
에러를 반환한다 userRepository.existbyuserId 메서드에 매개변수로 request.userId를 입력해서 상태정보가 존재하는지 확인하고 이미 존재하면 에러 던진다

 */

    @Override
    public UserStatusResponse find(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저의 상태 정보를 찾을 수 없습니다."));

        return new UserStatusResponse(userStatus);
    }
/* 매개변수로 userId를 받는다 userStatusRepository.findByUserId 메서드에 userId를 매개변수로 입력해서 반환된 UserStatus객체를
userStatus에 넣는다 반환된 userStatus가 있으면 그 userStatus를 반환하고 없을시 오류를 생성한다
userStatus를 UserStatusResponse로 변환하여 리턴값으로 반환한다
 */
    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(UserStatusResponse::new)
                .toList();
    }
/*Userstatus에 있는 모든 데이터를 가져와서 스트림 형식으로 바꾸고 .map부분에서 userstatus 객체를 하나씩 꺼내와서
Userstatusresponse라는 객체로 만들어서 리스트에 담는다
전부 담은 후 리턴값으로 UserstatusResponse가 담긴 리스트를 반환한다
.map(status -> new UserStatusResponse(status)) 이거랑 같은의미
 */

    @Override
    public void update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 유저 상태 정보를 찾을 수 없습니다."));
        userStatus.update(request.type());
        userStatusRepository.save(userStatus);
        System.out.println("업데이트 완료 ID: " + id + "의 상태가 변경되었습니다.");
    }
/* 매개변수로 id와 UserStatusUpdateRequest받아온다
userStatusRepository의 findById메소드에 id를 넣고 리턴값으로 반환된 객체를 userStatus에 담는다
반환된 userStatus가 있으면 그 userStatus를 반환하고 없을시 오류를 생성한다
받아온 타입을 꺼내서 업데이트하고 userStatus에 담는다
userStatusRepository.save 메서드의 매개변수로 userStatus를 입력한다
 */

    @Override
    public void updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId((userId))  // 문자열로부터
                .orElseThrow(() -> new RuntimeException("해당 유저의 상태 정보를 찾을 수 없습니다."));
        userStatus.update(request.type());
        userStatusRepository.save(userStatus);
    }
/* 매개변수로 userId와 UserStatusUpdateRequest를 받아온다
userStatusRepository의 findByUserId메소드에 userId를 넣고 리턴값으로 반환된 객체를 userStatus에 넣는다
 반환된 리턴값이 없을시 오류를 생성한다
 받아온 타입을 꺼내서 업데이트하고 userStatus에 담는다
 userStatusRepository.save 메서드의 매개변수로 userStatus를 입력한다
 */
    @Override
    public void delete(UUID id) {
        System.out.println("삭제 요청 들어옴! ID: " + id);
        userStatusRepository.deleteByUserId((id));
        System.out.println("삭제 완료!");
    }
}
//userStatusRepository.deleteByUserId메서드를 통해서 userStatusRepository 값을 삭제한다