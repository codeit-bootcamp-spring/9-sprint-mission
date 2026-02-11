package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.UpdateChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exepction.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("file")
public class FileChannelRepository implements ChannelRepository {
    private final Map<String, Channel> publicChannelNameMap = new ConcurrentHashMap<>();
    private final Map<UUID, Channel> publicChannelIdMap = new ConcurrentHashMap<>();
    private final Map<String, Channel> privateChannelNameMap = new ConcurrentHashMap<>();
    private final Map<UUID, Channel> privateChannelIdMap = new ConcurrentHashMap<>();
    private Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @PostConstruct
    public void init() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
        if(Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(channel -> {
                        if (channel.getChannelType() == ChannelType.PRIVATE) {
                            privateChannelNameMap.put(channel.getName(), channel);
                            privateChannelIdMap.put(channel.getId(), channel);
                        } else {
                            publicChannelNameMap.put(channel.getName(), channel);
                            publicChannelIdMap.put(channel.getId(), channel);
                        }
                    });
        } catch (IOException e) {
            throw new FailedInit("FileChannelRepository init failed");
        }
    }

    /// interface
    @Override
    public boolean save(Channel channel) {
        Path path = resolvePath(channel.getId());

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(channel);
            oos.flush();
            if (channel.getChannelType() == ChannelType.PUBLIC) {
                publicChannelNameMap.put(channel.getName(), channel);
                publicChannelIdMap.put(channel.getId(), channel);
            } else {
                privateChannelIdMap.put(channel.getId(), channel);
                privateChannelNameMap.put(channel.getName(), channel);
            }

            return true;
        } catch (IOException e) {
            throw new FailedCreate("Channel save failed");
        }
    }

    @Override
    public boolean save(UpdateChannelDto requestDto) {
        String oldName = requestDto.oldName();
        String newName = requestDto.newName();
        if(!isPresentChannel(oldName)) return false;
        if(privateChannelNameMap.containsKey(oldName))
            throw new DoNotUpdatePrivateChannel("Do not update private channel");
        Path path;
        Channel channel = publicChannelNameMap.get(oldName);

        path = resolvePath(channel.getId());

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(channel);
            oos.flush();

            publicChannelNameMap.put(newName, channel);
            publicChannelNameMap.remove(oldName);
            channel.channelUpdater(newName);

            return true;
        } catch (IOException e) {
            throw new FailedUpdate("Channel update failed");
        }
    }

    @Override
    public String readChannel(String name) {
        if(publicChannelNameMap.containsKey(name))
            return publicChannelNameMap.get(name).toString();
        if(privateChannelNameMap.containsKey(name))
            return privateChannelNameMap.get(name).toString();
        throw new FailedFound("Channel not found");
    }

    @Override
    public ChannelType getChannelType(String name) {
        if(publicChannelNameMap.containsKey(name))
            return publicChannelNameMap.get(name).getChannelType();
        if(privateChannelNameMap.containsKey(name))
            return privateChannelNameMap.get(name).getChannelType();
        throw new FailedFound("ChannelType not found");
    }

    @Override
    public UUID getChannelId(String name) {
        if(publicChannelNameMap.containsKey(name))
            return publicChannelNameMap.get(name).getId();
        if(privateChannelNameMap.containsKey(name))
            return privateChannelNameMap.get(name).getId();

        throw new FailedFound("ChannelId not found");
    }

    @Override
    public List<ResponseChannelDto> readAllChannel(String userName) {
        List<ResponseChannelDto> result = new ArrayList<>();

        /// public
        result.addAll(publicChannelNameMap.values().stream().map(this::requestChannelInfo).toList());

        /// private
        result.addAll(accessiblePrivateChannel(userName).stream().toList());
        return result;
    }

    @Override
    public List<ResponseChannelDto> findAllPrivateChannel(String userName) {
        if(accessiblePrivateChannel(userName).isEmpty())
            throw new NotFound("권한이 있는 Private Channel이 없습니다!");

        return new ArrayList<>(accessiblePrivateChannel(userName).stream().toList());
    }

    @Override
    public List<ResponseChannelDto> accessiblePrivateChannel(String userName) {
        List<ResponseChannelDto> requestDto = new ArrayList<>();
        privateChannelIdMap.values().stream()
                .filter(channel -> channel.getAccessibleUser() != null && channel.getAccessibleUser().containsKey(userName))
                .forEach(channel -> requestDto.add(requestChannelInfo(channel)));

        return requestDto;
    }

    @Override
    public ResponseChannelDto requestChannelInfo(Channel channel) {
        String name = channel.getName();
        UUID id = channel.getId();
        ChannelType type = channel.getChannelType();
        Instant createAt = channel.getCreateAt();
        Instant updateAt = channel.getUpdateAt();
        String createUser = channel.getCreateUser();
        Map<String, UUID> accessableUser = null;
        try {
            accessableUser = channel.getAccessibleUser();
        } catch (Exception ignore) {}

        return new ResponseChannelDto(name, id, type, createAt, updateAt, createUser, accessableUser);
    }

    @Override
    public void includePrivateChannel(String channelName, String userName, UUID userId) {
        privateChannelNameMap.get(channelName).addAccessibleUser(userName, userId);
    }

    @Override
    public void excludePrivateChannel(String channelName, String userName) {
        privateChannelNameMap.get(channelName).removeAccessibleUser(userName);
    }

    @Override
    public boolean deleteChannel(String name) {
        UUID id;
        boolean isPrivate = privateChannelNameMap.containsKey(name);

        if(isPrivate){
            id = privateChannelNameMap.get(name).getId();
        } else {
            id = publicChannelNameMap.get(name).getId();
        }

        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
            if(isPrivate){
                privateChannelNameMap.remove(name);
                privateChannelIdMap.remove(id);
            } else {
                publicChannelNameMap.remove(name);
                publicChannelIdMap.remove(id);
            }
            return true;
        } catch (IOException e) {
            throw new FailedDelete("Channel delete failed");
        }
    }

    @Override
    public void deleteAllChannel(String name) {
        List<Channel> channels = privateChannelNameMap.values().stream().filter(channel -> channel.getCreateUser().equals(name)).toList();

        channels.forEach(channel -> deleteChannel(channel.getName()));
    }


    @Override
    public boolean isPresentChannel(String name) {
        return publicChannelNameMap.containsKey(name) || privateChannelNameMap.containsKey(name);
    }

    @Override
    public boolean isCreatePrivateChannel(String name) {
        return !privateChannelNameMap.values().stream().filter(channel -> channel.getCreateUser().equals(name)).toList().isEmpty();
    }

    @Override
    public UUID channelNameToId(String name) {
        if(publicChannelNameMap.containsKey(name)) return publicChannelNameMap.get(name).getId();
        if(privateChannelNameMap.containsKey(name)) return privateChannelNameMap.get(name).getId();

        throw new NotFound("해당 채널을 찾을 수 없습니다");
    }

    @Override
    public String channelIdToName(UUID id) {
        if(publicChannelIdMap.containsKey(id)) return publicChannelIdMap.get(id).getName();
        if(privateChannelIdMap.containsKey(id)) return privateChannelIdMap.get(id).getName();

        throw new NotFound("해당 채널을 찾을 수 없습니다");
    }
}
