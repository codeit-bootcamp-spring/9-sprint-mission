package com.sprint.mission.discodeit.repository.file;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

// 파일별로 Lock을 관리하는 클래스
@Component
public class FileLockProvider {

  private final Map<Path, ReentrantLock> locks = new ConcurrentHashMap<>();

  public ReentrantLock getLock(Path path) {
    return locks.computeIfAbsent(path, k -> new ReentrantLock());
  }
}
