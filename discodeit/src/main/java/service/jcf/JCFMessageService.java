package service.jcf;

import entity.Message;
import service.ChannelService;
import service.MessageService;
import service.UserService;
import entity.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<Message>> channelMessagesIndex = new ConcurrentHashMap<>();
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message save(Message message) {
        if (userService.findById(message.getUserId()).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if (channelService.findById(message.getChannelId()).isEmpty()) {
            throw new IllegalArgumentException("Channel not found");
        }
        messageMap.put(message.getId(), message);
        channelMessagesIndex.computeIfAbsent(message.getChannelId(), k -> new ArrayList<>()).add(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(messageMap.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return new ArrayList<>(channelMessagesIndex.getOrDefault(channelId, Collections.emptyList()));
    }

    @Override
    public void update(Message message) {
        if (messageMap.containsKey(message.getId())) {
            messageMap.put(message.getId(), message);
        }
    }
    //최적화이슈: 두개에서 다지워야하는데 매세지가 많아지면 오래걸리는데 어떡함그러면
    @Override
    public boolean delete(UUID id) {
        Message removed = messageMap.remove(id);
        if (removed != null) {
            List<Message> messages = channelMessagesIndex.get(removed.getChannelId());
            if (messages != null) {
                messages.remove(removed);
            }
            return true;
        }
        return false;
    }
    //월권이슈: Unknown구현 해보고싶어서 만들긴했는데 이건 여기서 할일이 아닌거 같기도 하고
    @Override
    public String getAuthorName(UUID messageId) {
        return findById(messageId)
                .map(msg -> userService.findById(msg.getUserId()) // 메시지의 유저를 찾는다
                        .map(User::getDisplayName) // 유저가 있으면 이름을 가져온다
                        .orElse("Unknown")) //유저가 없으면 Unknown
                .orElse("삭제된 메시지"); //예오ㅣ
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        List<Message> messages = channelMessagesIndex.remove(channelId);
        if (messages != null) {
            for (Message m : messages) {
                messageMap.remove(m.getId());
            }
        }
    }
}