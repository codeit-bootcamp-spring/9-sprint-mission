package repository.jcf;

import entity.Message;
import repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {

    private final List<Message> data = new ArrayList<>();

    @Override
    public Message save(Message message) {
        Objects.requireNonNull(message, "message is null");
        UUID id = Objects.requireNonNull(message.getId(), "message.id is null");

        for (int i = 0; i < data.size(); i++) {
            if (Objects.equals(data.get(i).getId(), id)) {
                data.set(i, message);
                return message;
            }
        }
        data.add(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        if (id == null) return Optional.empty();

        for (Message m : data) {
            if (Objects.equals(m.getId(), id)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data);
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) return;
        data.removeIf(m -> Objects.equals(m.getId(), id));
    }
}
