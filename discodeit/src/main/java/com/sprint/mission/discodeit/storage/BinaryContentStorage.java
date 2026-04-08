package com.sprint.mission.discodeit.storage;

import org.springframework.core.io.Resource;
import java.util.UUID;


public interface BinaryContentStorage {


  void put(UUID id, byte[] bytes);


  Resource loadAsResource(UUID id);


  void delete(UUID id);
}