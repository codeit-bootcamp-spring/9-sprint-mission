package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.request.RequestCreateBinaryContentDto;
import com.sprint.mission.discodeit.dto.response.ResponseBinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> fileIdMap = new ConcurrentHashMap<>();

    @Override
    public UUID create(RequestCreateBinaryContentDto requestDto) {
        String type = requestDto.contentType();
        String file = requestDto.filename();
        byte[] bytes = requestDto.bytes();
        BinaryContent binaryContent = new BinaryContent(type, file, bytes);

        fileIdMap.put(binaryContent.getId(), binaryContent);

        return binaryContent.getId();
    }

    @Override
    public ResponseBinaryContentDto find(UUID id) {
        return response(fileIdMap.get(id));
    }

    @Override
    public List<ResponseBinaryContentDto> findAllByIdIn(List<UUID> ids) {
        List<ResponseBinaryContentDto> result = new ArrayList<>();
        ids.forEach(id -> result.add(response(fileIdMap.get(id))));
        return result;
    }

    private ResponseBinaryContentDto response(BinaryContent binaryContent) {
        return new ResponseBinaryContentDto(
                binaryContent.getId(),
                binaryContent.getCreateAt(),
                binaryContent.getFileName(),
                binaryContent.getFileExtension(),
                binaryContent.getType(),
                binaryContent.getBytes()
        );
    }

    @Override
    public boolean delete(UUID id) {
        fileIdMap.remove(id);
        return true;
    }

    @Override
    public void delete(List<UUID> ids) {
        ids.forEach(this::delete);
    }
}
