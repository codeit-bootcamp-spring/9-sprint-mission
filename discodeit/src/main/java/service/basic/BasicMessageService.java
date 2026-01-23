package service.basic;

import entity.Channel;
import entity.Message;
import repository.ChannelRepository;
import repository.MessageRepository;
import repository.UserRepository;
import service.MessageService;
import service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicMessageService(
            MessageRepository messageRepository,
            ChannelRepository channelRepository,
            UserRepository userRepository)
    {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Message update(UUID id, String chat) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
        message.update(chat);
        return messageRepository.save(message);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>();
    }

    @Override
    public Message create(String chat, UUID channelId, UUID authorID) {
        Message message = new Message(chat, channelId, authorID);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID id) {
        return null;
    }

}