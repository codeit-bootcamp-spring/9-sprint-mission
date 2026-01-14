package service;

import entity.Channel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel save(Channel channel);
    Optional<Channel> findById(UUID id);
    Optional<Channel> findByName(String name); // 이름으로 찾기 기능 추가
    List<Channel> findAll();
    void update(Channel channel);
    boolean delete(UUID id);
}