package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto create(BinaryContentCreateRequest request);

    BinaryContentDto find(UUID id);

    List<BinaryContentDto> findAllByIn(List<UUID> idList);

    List<BinaryContentDto> uploadFiles(List<MultipartFile> files);

    void delete(UUID id);

}
