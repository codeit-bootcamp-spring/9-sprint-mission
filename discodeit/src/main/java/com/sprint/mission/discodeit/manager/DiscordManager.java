package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 추가: 로그용
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Slf4j // 롬복의 로그 어노테이션
@Component
@RequiredArgsConstructor
public class DiscordManager implements ChatManager {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final CategoryService categoryService;

    @Override
    public String getAuthorName(UUID messageId) {
        return messageService.findById(messageId)
                .flatMap(msg -> userService.findById(msg.getUserId())) // Optional 중첩 제거
                .map(User::getDisplayName)
                .orElse("Unknown 또는 삭제된 메시지");
    }

    @Override
    public Optional<Message> sendMessage(UUID userId, UUID channelId, String content) { // 1. 리턴 타입 변경
        if (userService.findById(userId).isEmpty()) {
            System.out.println("전송 실패: 존재하지 않는 유저입니다.");
            return Optional.empty(); // 2. null 대신 빈 상자 반환
        }

        if (channelService.findById(channelId).isEmpty()) {
            System.out.println("전송 실패: 존재하지 않는 채널입니다.");
            return Optional.empty(); // 3. 여기도 동일
        }

        Message newMessage = new Message(content, userId, channelId);
        Message savedMessage = messageService.save(newMessage);

        return Optional.of(savedMessage); // 4. 성공 시 상자에 담아서 반환
    }

    private boolean validateUserAndChannel(UUID userId, UUID channelId) {
        if (userService.findById(userId).isEmpty()) {
            log.error("전송 실패: 존재하지 않는 유저 ID: {}", userId);
            return false;
        }
        if (channelService.findById(channelId).isEmpty()) {
            log.error("전송 실패: 존재하지 않는 채널 ID: {}", channelId);
            return false;
        }
        return true;
    }

    @Override
    public void deleteCategorySafely(UUID categoryId) {
        List<Channel> channels = channelService.findAll().stream()
                .filter(c -> isAssociatedWithCategory(c, categoryId)) // 가독성 향상
                .toList();

        channels.forEach(channel -> {
            channel.update(channel.getName(), channel.getType(), channel.getDescription(), null);
            channelService.update(channel);
        });

        categoryService.delete(categoryId);
        log.info("카테고리(ID: {}) 삭제 완료. 관련 채널 수: {}", categoryId, channels.size());
    }

    private boolean isAssociatedWithCategory(Channel channel, UUID categoryId) {
        return channel.getCategory() != null && channel.getCategory().getId().equals(categoryId);
    }
}