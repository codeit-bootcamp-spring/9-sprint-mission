package service;

import entity.User;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface UserService {
    // 1. 저장 (C++의 insert 개념)
    User save(User user);

    // 2. 고유 ID로 조회 (가장 정확한 방법)
    // Optional은 null 체크를 강제하여 안정성을 높입니다 (C++의 스마트 포인터와 유사)
    Optional<User> findById(UUID id);

    // 3. 별명으로 조회 (필요한 경우에만 별도로 제공)
    Optional<User> findByDisplayName(String displayName);

    // 4. 전체 목록 조회
    List<User> findAll();

    // 5. 수정 (객체 자체를 넘겨서 내부 상태를 반영)
    void update(User user);

    // 6. 삭제 (고유 ID 기준)
    boolean delete(UUID id);
}