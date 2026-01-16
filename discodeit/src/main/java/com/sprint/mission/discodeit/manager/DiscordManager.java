package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.*;

import java.util.UUID;
import java.util.List;

public class DiscordManager implements ChatManager {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final CategoryService categoryService;

    public DiscordManager(UserService userService, ChannelService channelService,
                          MessageService messageService, CategoryService categoryService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
        this.categoryService = categoryService;
    }

    @Override
    public String getAuthorName(UUID messageId) {
        // [수정] messageService.을 붙여서 해당 서비스의 메서드를 호출해야 합니다.
        return messageService.findById(messageId)
                .map(msg -> userService.findById(msg.getUserId())
                        .map(User::getDisplayName)
                        .orElse("Unknown"))
                .orElse("삭제된 메시지");
    }

    @Override
    public Message sendMessage(UUID userId, UUID channelId, String content) {
        if (userService.findById(userId).isEmpty()) {
            System.out.println("전송 실패: 존재하지 않는 유저입니다.");
            return null;
        }

        if (channelService.findById(channelId).isEmpty()) {
            System.out.println("전송 실패: 존재하지 않는 채널입니다.");
            return null;
        }

        // 모든 검증 통과 시 저장
        Message newMessage = new Message(content, userId, channelId);
        return messageService.save(newMessage);
    }

    public void deleteCategorySafely(UUID categoryId) {
        // 1. 해당 카테고리를 가진 채널들 필터링
        List<Channel> channels = channelService.findAll().stream()
                .filter(c -> c.getCategory() != null && c.getCategory().getId().equals(categoryId))
                .toList();

        // 2. [Orphan 처리] 채널의 카테고리 관계만 끊어줌
        for (Channel channel : channels) {
            // Channel 엔티티의 업데이트 기능을 사용하여 카테고리를 null로 변경
            channel.update(channel.getName(), channel.getType(), channel.getDescription(), null);
            channelService.update(channel);
        }

        // 3. 마지막으로 관리 목록에서 카테고리 삭제
        categoryService.delete(categoryId);
        System.out.println("카테고리가 삭제되었습니다. 관련 채널들은 '미지정' 상태로 유지됩니다.");
    }
}