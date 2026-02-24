package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserStatusRepository implements UserStatusRepository {
    private final List<UserStatus> userStatuses = new ArrayList<>();

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatuses.stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }
//findByUserId 메서드를 stream 방식으로 실행하는데 필터를 거쳐 status의 id가 매개변수로 받은 id랑 같은면
//가장 첫번째 UserStatus 반환한다 리턴값(Optional)
    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatuses.add(userStatus);
        return userStatus;
    }
//매개변수로 받아온 userStatuses에 새로운 데이터를 추가하고 저장된 객체를 리턴값으로 반환한다
    @Override
    public Optional<UserStatus> findById(UUID id) {
        return userStatuses.stream()
                .filter(status -> status.getId()==id)
                .findFirst();
    }
//findById 메서드를 stream 방식으로 실행하는데 필터를 거쳐 status의 id가 매개변수로 받은 id랑 같은면
//가장 첫번째 UserStatus 반환한다 리턴값(Optional)

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(userStatuses);
    }
//userStatuses를 직접 반환하지 않고, new ArrayList<>()를 통해 복사본을 생성하여 반환한다
    @Override
    public void deleteByUserId(UUID userId) {
        userStatuses.removeIf(status -> status.getUserId().equals(userId));
    }
//자바 컬렉션의 removeIf 메서드를 활용하여 리스트 내의 UserStatus 매개변수로 받은 userId와 일치하는 데이터를 찾아 즉시 삭제한다
    @Override
    public boolean existsByUserId(UUID userId) {
        return userStatuses.stream()
                .anyMatch(userStatus -> userStatus.getUserId().equals(userId));
    }
}
/*UserStatuses를 스트림 형식으로 바꾸고 userStatuses의 데이터 중 userStatus의 UserId가 매개변수로 받아온
UserId와 일치하는 값이 하나라도 있다면 True를 반환, 일치하는 값이 하나도 없다면 False를 반환한다
 */