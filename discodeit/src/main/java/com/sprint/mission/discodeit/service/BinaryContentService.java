package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentCreateRequest request);

    BinaryContent find(UUID id);

    List<BinaryContent> findAllByIn(List<UUID> idList);

    void delete(UUID id);

    BinaryContent uploadFile(MultipartFile file);
}
