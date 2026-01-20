package service.Basic;

import entity.Message;
import repository.ChannelRepository;
import repository.MessageRepository;
import repository.UserRepository;
import service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    //private final Map<UUID, List<UUID>> messagesByUser;

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository){
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Message create(UUID writerId, UUID channelId, String content) {
        Message newMessage = new Message(writerId, channelId, content);
        messageRepository.save(newMessage);
        return newMessage;
    }

    @Override
    public void remove(UUID id) {
        messageRepository.remove(id);
    }

    @Override
    public Message findByID(UUID id) {
        return messageRepository.findByID(id);
    }

    @Override
    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateContent(UUID id, String newContent) {
        Message target = messageRepository.findByID(id);
        if (target == null){
            throw new IllegalStateException("메시지 수정 실패 (해당 메시지가 존재하지 않음) | 메시지ID: " + id);
        }
        target.updateContent(newContent);
        messageRepository.save(target);
        return target;
    }

    // 안할 예정
    @Override
    public List<Message> findByUserID(UUID userId) {
        return List.of();
    }
}
