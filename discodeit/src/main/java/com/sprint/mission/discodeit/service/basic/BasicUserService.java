package com.sprint.mission.discodeit.service.basic;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    @Override
    public User save(User user) {

        boolean isDuplicate = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) ||
                        u.getPhoneNumber().equals(user.getPhoneNumber()));

        if (isDuplicate) {
            System.out.println("저장 실패: 이미 존재하는 이메일 또는 전화번호입니다. (" + user.getDisplayName() + ")");
            return null; // 저장을 하지 않고 null을 반환하여 main에 알림
        }

        userRepository.save(user);
        return user;
    }

    @Override
    public List<User> findAllByDisplayNameKeyword(String keyword) {
        return userRepository.findAll().stream()
                .filter(u -> u.getDisplayName().contains(keyword))
                .toList();
    }

    @Override
    public Optional<User> findById(UUID id) { return userRepository.findById(id); }

    @Override
    public Optional<User> findByDisplayName(String displayName) { return userRepository.findByDisplayName(displayName); }

    @Override
    public List<User> findAll() { return userRepository.findAll(); }


    private boolean isDuplicate(String email, String phone, UUID currentUserId) {
        return userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(currentUserId)) // 나 자신은 제외
                .anyMatch(u -> u.getEmail().equals(email) || u.getPhoneNumber().equals(phone));
    }

    @Override
    public void update(User user) {
        if (isDuplicate(user.getEmail(), user.getPhoneNumber(), user.getId())) {
            // 타이밍 안맞음 문제 -> 모든 출력을 System.out으로 통일해서 타이밍을 맞춥니다.
            System.out.println("업데이트 실패: 이미 사용 중인 이메일 또는 전화번호입니다.");

            return;
        }

        userRepository.save(user);
        System.out.println("업데이트 성공: " + user.getDisplayName());
    }


    @Override
    public boolean delete(UUID id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.delete(id);
            return true;
        }
        return false;
    }
}