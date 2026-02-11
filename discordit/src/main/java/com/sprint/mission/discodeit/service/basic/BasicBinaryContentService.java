package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateBinaryContentDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public UUID create(CreateBinaryContentDto createBinaryContentDto) {
        return binaryContentRepository.create(createBinaryContentDto);
    }

    public String find(UUID id) {
        return binaryContentRepository.find(id);
    }

    public List<String> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids);
    }

    public boolean delete(UUID id) {
        return binaryContentRepository.delete(id);
    }
}
