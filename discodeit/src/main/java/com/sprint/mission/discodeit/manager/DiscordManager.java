package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.factory.ServiceFactory;

import java.util.UUID;
import java.util.List;

public class DiscordManager implements ChatManager {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final CategoryService categoryService;

    // [수정] 싱글톤을 위해 생성자는 하나만(private) 남겨야 합니다.
    private DiscordManager() {
        this.userService = ServiceFactory.getUserService();
        this.channelService = ServiceFactory.getChannelService();
        this.messageService = ServiceFactory.getMessageService();
        this.categoryService = ServiceFactory.getCategoryService();
    }

    private static class InstanceHolder {
        private static final DiscordManager INSTANCE = new DiscordManager();
    }

    public static DiscordManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    // [삭제] 기존에 있던 'public DiscordManager(...)'는 팩토리 패턴과 충돌하므로 지웠습니다.

    @Override
    public String getAuthorName(UUID messageId) {
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

        Message newMessage = new Message(content, userId, channelId);
        return messageService.save(newMessage);
    }

    public void deleteCategorySafely(UUID categoryId) {
        List<Channel> channels = channelService.findAll().stream()
                .filter(c -> c.getCategory() != null && c.getCategory().getId().equals(categoryId))
                .toList();

        for (Channel channel : channels) {
            channel.update(channel.getName(), channel.getType(), channel.getDescription(), null);
            channelService.update(channel);
        }

        categoryService.delete(categoryId);
        System.out.println("카테고리가 삭제되었습니다. 관련 채널들은 '미지정' 상태로 유지됩니다.");
    }
}