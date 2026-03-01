package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.UploadFileException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
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
                request.contentType(),
                request.data()
        );

        binaryContentRepository.save(binaryContent);

        return binaryContent;
    }

    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findByID(id).orElseThrow(() -> new NoSuchElementException(
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
        binaryContentRepository.remove(id);
    }

    @Override
    public BinaryContent uploadFile(MultipartFile file) {
        String contentType = MediaTypeFactory.getMediaType(file.getOriginalFilename())
                .map(MediaType::toString)
                .orElse("application/octet-stream");

        try {
            byte[] fileBytes = file.getBytes();

            return this.create(new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    contentType,
                    fileBytes
            ));
        } catch (IOException e) {
            throw new UploadFileException("파일 업로드 실패 - " + e.getMessage());
        }
    }

    @Override
    public List<BinaryContent> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        return files.stream()
            .map(this::uploadFile)
            .toList();
    }
}
