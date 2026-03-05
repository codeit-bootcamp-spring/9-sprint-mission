package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

    public UUID put(UUID binaryContentId, byte[] bytes) {
        // 이미 DB에 직접 저장하도록 고쳤으므로 이 로컬 저장 로직은 무시합니다.
        return binaryContentId;
    }

    public InputStream get(UUID binaryContentId) {
        return null;
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto metaData) {
        return null;
    }
}