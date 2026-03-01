package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFBinaryContentRepository implements BinaryContentRepository {

  private final Map<UUID, BinaryContent> storage = new ConcurrentHashMap<>();

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    storage.put(binaryContent.getId(), binaryContent);
    return binaryContent;
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return ids.stream()
        .map(storage::get)
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return storage.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    storage.remove(id);
  }
}