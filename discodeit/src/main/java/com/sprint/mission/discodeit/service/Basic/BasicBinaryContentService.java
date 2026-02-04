package com.sprint.mission.discodeit.service.Basic;

import com.sprint.mission.discodeit.DTO.BinaryContentService.Request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(CreateBinaryContentRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.ownerType(),
                request.ownerId(),
                request.data()
        );

        binaryContentRepository.save(binaryContent);

        return binaryContent;
    }

    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findByID(id).orElseThrow();
    }

    @Override
    public List<BinaryContent> findAllByIn(List<UUID> idList) {
        return idList.stream()
                .map(this::find)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.remove(id);
    }
}
