package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public BinaryContent create(BinaryContentDto.createDto createDto) {
        UUID id = UUID.randomUUID();
        String Path = "data/binary/" +id  + ".ser";
         BinaryContent binaryContent= new BinaryContent(
                 id,
                Path,
                createDto.contentType(),
                createDto.fileName()

        );
         return binaryContentRepository.save(binaryContent);
    }

    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("파일이 없습니다."));

    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(binaryContentRepository::findById)
                .flatMap(Optional::stream)
                .toList();
                }


    @Override
    public boolean delete(UUID id) {
        if(!binaryContentRepository.existById(id)){
            throw new NoSuchElementException("삭제할 파일이 없습니다.");
        }
        binaryContentRepository.deleteById(id);
        return true;
    }
}
