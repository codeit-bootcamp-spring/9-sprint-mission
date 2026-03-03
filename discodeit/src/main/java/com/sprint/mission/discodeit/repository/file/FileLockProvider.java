package com.sprint.mission.discodeit.repository.file;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

/**
 * 파일 저장소의 동시성 제어를 위한 Lock 제공 클래스
 *
 * 동일한 파일 경로에 대해 동일한 ReentrantLock을 반환하여,
 * 여러 스레드가 동시에 같은 파일을 읽거나 쓸 때 발생하는 경쟁 상태(Race Condition)를 방지합니다.
 */
@Component
public class FileLockProvider {

  /** 파일 경로별 Lock을 관리하는 맵 (Thread-safe) */
  private final Map<Path, ReentrantLock> locks = new ConcurrentHashMap<>();

  /**
   * 지정된 파일 경로에 대한 Lock을 반환합니다.
   * 해당 경로의 Lock이 없으면 새로 생성하여 반환합니다.
   *
   * @param path Lock을 적용할 파일 경로
   * @return 해당 경로에 대한 ReentrantLock
   */
  public ReentrantLock getLock(Path path) {
    return locks.computeIfAbsent(path, k -> new ReentrantLock());
  }
}
