package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
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
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentDto create(String fileName, byte[] data, String contentType) {

        BinaryContent content = new BinaryContent(fileName, data, contentType);
        binaryContentRepository.save(content);

        return binaryContentMapper.toDto(content);
    }

    @Override
    public BinaryContentDto findById(UUID id) {

        BinaryContent content = binaryContentRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("BinaryContent를 찾을 수 없습니다: " + id)
            );

        return binaryContentMapper.toDto(content);
    }

    @Override
    public BinaryContent findEntityById(UUID id) {

        return binaryContentRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("BinaryContent를 찾을 수 없습니다: " + id)
            );
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {

        return binaryContentRepository.findAllByIdIn(ids).stream()
            .map(binaryContentMapper::toDto)
            .toList();
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.delete(id);
    }
}