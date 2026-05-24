package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.UploadFileException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        log.info("파일 업로드 시도: fileName={}, size={}, contentType={}", request.fileName(), request.size(), request.contentType());
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.size(),
                request.contentType()
        );

        binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), request.bytes());
        log.debug("파일 업로드 완료: id={}", binaryContent.getId());
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentDto find(UUID id) {
        return binaryContentMapper.toDto(binaryContentRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
            "BinaryContent with id " + id + " not found")));
    }

    @Override
    public List<BinaryContentDto> findAllByIn(List<UUID> idList) {
        return binaryContentRepository.findAllById(idList).stream()
            .map(binaryContentMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public List<BinaryContentDto> uploadFiles(List<MultipartFile> files){
        Map<UUID, MultipartFile> fileMap = new HashMap<>();
        List<BinaryContent> binaryContents = files.stream()
            .map(file->{
                BinaryContent newBinaryContent = new BinaryContent(
                  file.getOriginalFilename(),
                  file.getSize(),
                  file.getContentType()
                );
                fileMap.put(newBinaryContent.getId(), file);
                return newBinaryContent;
            })
            .toList();

        List<BinaryContent> savedContents = binaryContentRepository.saveAll(binaryContents);

        return savedContents.stream()
            .map(content -> {
                MultipartFile file = fileMap.get(content.getId());
                try {
                    binaryContentStorage.put(content.getId(), file.getBytes());
                    return binaryContentMapper.toDto(content);
                } catch (IOException e) {
                    throw new UploadFileException("파일 저장 실패: " + file.getOriginalFilename() + " / "+ e);
                }
            })
            .toList();
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }
}
