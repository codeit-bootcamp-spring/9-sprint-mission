package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.BinaryContentJpaRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentJpaRepository repository;
    private final BinaryContentStorage storage;

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {

        BinaryContent entity =
            new BinaryContent(
                request.fileName(),
                (long) request.bytes().length,
                request.contentType()
            );

        repository.save(entity);

        if (request.bytes() != null) {
            storage.put(entity.getId(), request.bytes());
        }

        return entity;
    }

    @Override
    public BinaryContentDto get(UUID id) {

        BinaryContent entity = repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("BinaryContent not found"));

        return new BinaryContentDto(
            entity.getId(),
            entity.getFileName(),
            entity.getContentType(),
            entity.getSize()
        );
    }


    @Override
    public BinaryContent find(UUID binaryContentId) {
        return repository.findById(binaryContentId)
            .orElseThrow(() -> new NoSuchElementException("BinaryContent not found"));
    }


    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        return repository.findAllById(binaryContentIds);
    }

    @Override
    public void delete(UUID binaryContentId) {
        repository.deleteById(binaryContentId);
    }
}