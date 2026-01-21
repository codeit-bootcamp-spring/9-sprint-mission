package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List; // 여러 User를 반환하기 위해 필요
import java.util.UUID; // id 기반 조회/수정/삭제에 필요

 /** [Interface]
 * 목적: "기능의 목록(메뉴판)"만 정의한다.
 * 특징: 실제 동작 코드(중괄호 {})가 없다.
 * - 구현체(JCFUserService, FileUserService 등)가 "무조건 이 기능들을 제공해야 한다"를 강제
 */
public interface UserService {
    // 1. 생성
    void join(User user);
    // 2. 단건 조회
    User findById(UUID id);
    // 2-1. 다건 조회
    List<User> findAll();
    // 3. 수정
    boolean update(UUID id, String nickname, String phoneNumber, String password);
    // 4. 삭제
    boolean delete(UUID id);
}



