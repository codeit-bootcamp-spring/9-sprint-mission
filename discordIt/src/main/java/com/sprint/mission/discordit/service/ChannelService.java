package service;

import entity.Channel;
import entity.ChannelType;
import java.util.List;
import java.util.UUID;

// 인터페이스 : 어떤 역할을 수행할 것인지 정해둔 메뉴판, 목록만 정의하는 곳, 실제 행동은 하지 않음
public interface ChannelService {

    Channel create(ChannelType type, String name, String description);

    Channel find(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, String name, String description);

    void delete(UUID id);
}