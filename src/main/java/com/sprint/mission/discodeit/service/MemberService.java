package com.sprint.mission.discodeit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  @Transactional
  public String createMember(String name) {

    memberRepository.save(
        Member.builder()
            .name(name)
            .build()
    );

    // 특정 이름이면 실패 처리
    if ("error".equals(name)) {

      // 예외는 던지지 않음
      TransactionAspectSupport
          .currentTransactionStatus()
          .setRollbackOnly();

      return "비즈니스 실패 - 롤백됨";
    }

    return "성공";
  }
}
