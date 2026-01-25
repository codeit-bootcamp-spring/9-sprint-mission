package service.Basic;

import entity.Channel;
import entity.Message;
import repository.ChannelRepository;
import repository.MessageRepository;
import repository.UserRepository;
import service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository){
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Message create(UUID channelId, UUID writerId, String content) {
        Message newMessage = new Message(channelId, writerId, content);
        messageRepository.save(newMessage);
        UUID newMsgId = newMessage.getId();

        Channel channel = channelRepository.findByID(channelId);
        if (!channel.addMessage(newMsgId)){
            messageRepository.remove(newMsgId);
        }
        return newMessage;
    }

    @Override
    public void remove(UUID id) {
        Message removeMessage = messageRepository.findByID(id);
        UUID channelId = removeMessage.getChannel();
        messageRepository.remove(id);

        Channel channel = channelRepository.findByID(channelId);
        if (!channel.removeMessage(id)){
            messageRepository.save(removeMessage);
        }
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
}
