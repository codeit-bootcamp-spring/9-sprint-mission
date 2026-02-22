package com.sprint.mission.discodeit.repository.file;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class FileLockProvider {

  private final Map<Path, ReentrantLock> locks = new ConcurrentHashMap<>();

  public ReentrantLock getLock(Path path) {
    Path key = path.toAbsolutePath().normalize();
    return locks.computeIfAbsent(key, k -> new ReentrantLock());
  }
}
