package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateRequest request) {
        User user = new User(request.username(), request.email(), request.password());
        User savedUser = userRepository.save(user);
        UserStatus status = new UserStatus(savedUser.getId());
        userStatusRepository.save(status);

        return savedUser;
    }
/* 매개변수로 UserCreateRequest를 받아서 request에 이름,이메일,비밀번호로 새유저를 만들고
userRepository.save메서드의 매개변수로 user를 넣어서 리턴값으로 반환된 user를 saveduser로 저장한다
savedUser의 id로 new UserStatus로 만들어서 status로 저장하고 userStatusRepository.save의 메서드 매개변수로
입력해서 호출한다 return savedUser반환한다
*/
    @Override
    public UserStatusResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("User status not found"));

        return new UserStatusResponse(
                user.getUsername(),
                status.isOnline(),
                status.getUpdatedAt().toString()
        );
    }
/*매개변수로 userId를 받는다 userRepository.findById메서드에 userId를 매개변수로 입력해서 리턴값으로 반환된 User객체를 user에넣는다
만약에 반환된 user가 없으면 에러를 던진다 userStatusRepository.findById메서드에 userId를 매개변수로 입력해서 리턴값으로 반환된 UserStatus객체를 status에넣는다
만약에 반환된 UserStatus가 없으면 에러를 던진다 리턴값으로 new UserStatusResponse객체를 반환한다 UserStatusResponse의 값은 위에서 찾은 user의 이름과
status의 온라인상태,업데이트 시간이 들어간다
 */
    @Override
    public List<UserStatusResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> find(user.getId()))
                .toList();
    }
/*  유저에 있는 모든 데이터를 가져와서 스트림 형식으로 바꾸고 .map부분에서 user객체를 하나씩 꺼내고
 find 메소드를 이용해서 id로 추출한 유저정보를 UserStatusResponse변환하고 리스트 형식으로 만든다
 */
    @Override
    public User update(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        user.update(
                request.name(),
                request.email(),
                request.password()
        );
        return userRepository.save(user);
    }
/* 매개변수로 userid와 UserUpdateRequest를 받는다  userRepository.findById메서드에 useerid를 매개변수로 입력해서 리턴값으로 반환된 User객체를
user에 넣는다 반환된 user가 없으면 에러를 던진다. user.update메서드를 통해 request에 담긴 이름,이메일,비밀번호로 변경한다.
리턴값으로 userRepository.save 메서드의 반환값을 보낸다
 */

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found");
        }
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}
/* 매개변수로 userid를 받아서 userRepository.existsById메서드로 userId가 있는지 확인하고 없으면 에러를 던진다
userStatusRepository.deleteByUserId메서드와
userRepository.deleteById메서드를 통해서 userStatusRepository와  userRepository값을 삭제한다
 */