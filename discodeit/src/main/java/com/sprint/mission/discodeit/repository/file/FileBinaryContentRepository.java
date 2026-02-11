package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> data = new ConcurrentHashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }
//입력받은 매개변수 binaryContent id와 값을 data에 입력하고 리턴값으로 binaryContent 받는다
    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    //안에 아무것도 없으면 null반환한다.
    //data.get(id) map에서 매개변수로 받은 id를 사용해 data에서 꺼낸다

    @Override
    public Optional<BinaryContent> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(content -> content.getUserId().equals(userId))
                .findFirst();
    }
//data에 저장된 모든 values(객체)를 하나씩 꺼내서 스트림 방식으로 변환한다.
//BinaryContent 객체에서 getUserId(메소드)호출하고 추출한 id가 매개변수로 받은 userid와 값이 같은 가장 첫번째 값을 반환한다
    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }
}
// data에서 매개변수로 받은 id와 일치하는 key가 있다면 삭제한다