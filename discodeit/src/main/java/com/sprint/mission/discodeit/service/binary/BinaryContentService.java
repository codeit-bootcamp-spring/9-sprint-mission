package com.sprint.mission.discodeit.service.binary;

import com.sprint.mission.discodeit.DTO.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.Optional;
import java.util.UUID;

public class BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentService(BinaryContentRepository binaryContentRepository) {
        this.binaryContentRepository = binaryContentRepository;
    }

    public BinaryContent createBinaryContent(BinaryContentDTO.CreateBinaryContentDTO dto){

        BinaryContent binaryContent = new BinaryContent(
                dto.content(),
                dto.contentType(),
                dto.fileName()
        );

        return binaryContentRepository.save(binaryContent);
    }

    public Optional<BinaryContent> findByBinaryContentId(UUID binaryContentId){
        return binaryContentRepository.findById(binaryContentId);
    }

    public Optional<BinaryContent> findAllByIdIn(Iterable<UUID> ids){
        return binaryContentRepository.findAllByIdIn(ids);
    }

//    public BinaryContent save(BinaryContent binaryContent){
//        return binaryContentRepository.save(binaryContent);
//    }

    public void deleteById(UUID id){
        binaryContentRepository.deleteById(id);
    }


}
