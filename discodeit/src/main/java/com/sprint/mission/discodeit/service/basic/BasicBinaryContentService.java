package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.BinaryContentJpaRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService {

    private final BinaryContentJpaRepository binaryContentRepository;

    public List<BinaryContent> findAllByIds(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllById(binaryContentIds)
            .stream()
            .collect(Collectors.toList());
    }

    public BinaryContent save(BinaryContent binaryContent) {
        return binaryContentRepository.save(binaryContent);
    }

    public BinaryContent findById(UUID id) {
        return binaryContentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found"));
    }

    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }
}
