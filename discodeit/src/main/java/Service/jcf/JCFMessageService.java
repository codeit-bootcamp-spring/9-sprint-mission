package Service.jcf;

import Service.MessageService;
import entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    //저장소
    private final Map<UUID, User> data = new HashMap<>();
}
