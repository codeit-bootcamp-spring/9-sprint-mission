package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.RequestCreateBinaryContentDto;
import com.sprint.mission.discodeit.dto.response.ResponseBinaryContentDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public UUID create(RequestCreateBinaryContentDto requestCreateBinaryContentDto) {
        return binaryContentRepository.create(requestCreateBinaryContentDto);
    }

    public ResponseBinaryContentDto find(UUID id) {
        return binaryContentRepository.find(id);
    }

    public List<ResponseBinaryContentDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids);
    }

    public boolean delete(UUID id) {
        return binaryContentRepository.delete(id);
    }
}
