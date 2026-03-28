package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.request.RequestCreateBinaryContentDto;
import com.sprint.mission.discodeit.dto.response.ResponseBinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {
    UUID create(RequestCreateBinaryContentDto requestDto);

    ResponseBinaryContentDto find(UUID id);

    List<ResponseBinaryContentDto> findAllByIdIn(List<UUID> ids);

    boolean delete(UUID id);

    void delete(List<UUID> ids);
}
