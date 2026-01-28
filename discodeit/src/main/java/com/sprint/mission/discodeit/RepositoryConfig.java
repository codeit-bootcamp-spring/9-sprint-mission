import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import repository.ChannelRepository;
import repository.MessageRepository;
import repository.UserRepository;
import repository.file.FileChannelRepository;
import repository.file.FileMessageRepository;
import repository.file.FileUserRepository;

@Configuration
public class RepositoryConfig {

    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository();
    }

    @Bean
    public ChannelRepository channelRepository() {
        return new FileChannelRepository();
    }

    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository();
    }
}
