package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.UploadFileException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.Collections;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.size(),
                request.contentType()
        );

        binaryContentRepository.save(binaryContent);

        return binaryContent;
    }

    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
            "BinaryContent with id " + id + " not found"));
    }

    @Override
    public List<BinaryContent> findAllByIn(List<UUID> idList) {
        return idList.stream()
                .map(this::find)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }
}
