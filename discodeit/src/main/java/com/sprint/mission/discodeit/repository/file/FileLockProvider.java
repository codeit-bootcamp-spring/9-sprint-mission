package com.sprint.mission.discodeit.repository.file;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

@Component
public class FileLockProvider {
    //열쇠 보관함 (파일 경로 -> 열쇠)
    private final Map<Path, ReentrantLock> locks = new ConcurrentHashMap<>();
    //이 파일(path)에 맞는 열쇠 주세요, 요청하는 메소드
    public ReentrantLock getLock(Path path) {
        //lockMap에서 path에 맞는 열쇠를 찾아보고 없으면(absent) 새로만들어줘(new)
        return locks.computeIfAbsent(path, k -> new ReentrantLock());
    }
}
