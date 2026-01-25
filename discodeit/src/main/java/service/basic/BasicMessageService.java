package service.basic;

import entity.Channel;
import entity.Message;
import entity.User;
import repository.MessageRepository;
import service.ChannelService;
import service.MessageService;
import service.UserService;

import java.util.List;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;
    public  BasicMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void sendMessage(Message message) {
        if (message.getContent() == null) {
            throw new NullPointerException("메시지를 입력하세요");
        }

        messageRepository.sendMessage(message);
    }


    @Override
    public List<Message> getMessages() {
        return messageRepository.getMessages();
    }

    @Override
    public List<Message> getSenderMessages(User sender) {
        return messageRepository.getSenderMessages(sender);
    }

    @Override
    public List<Message> getReceiverMessages(User receiver)
    {
       return  messageRepository.getReceiverMessages(receiver);
    }

    @Override
    public boolean deleteMessage(String message, User receiver)
    {
        return messageRepository.deleteMessage(message,receiver);
    }
}
