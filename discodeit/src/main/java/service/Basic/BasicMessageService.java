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
    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository){
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
    }


    @Override
    public Message create(UUID channelId, UUID writerId, String content) {
        Message newMessage = new Message(channelId, writerId, content);
        messageRepository.save(newMessage);
        UUID newMsgId = newMessage.getId();

        // 근데 이러면 메시지 만들 때마다 채널도 수정해야함 불러야함
        Channel channel = channelRepository.findByID(channelId);
        if (!channel.addMessage(newMsgId)){
            messageRepository.remove(newMsgId);
        }
        channelRepository.save(channel);
        return newMessage;
    }

    @Override
    public void remove(UUID id) {
        Message removeMessage = messageRepository.findByID(id);
        messageRepository.remove(id);

        UUID channelId = removeMessage.getChannel();
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
