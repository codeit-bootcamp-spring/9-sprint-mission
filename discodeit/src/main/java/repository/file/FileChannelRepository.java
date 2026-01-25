package repository.file;

import entity.Channel;
import entity.User;
import repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION=".ser";
    public FileChannelRepository(){
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"),"file-data-map", Channel.class.getSimpleName());
        if(Files.notExists(DIRECTORY)){
            try{
                Files.createDirectories(DIRECTORY);
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(String name){
        return DIRECTORY.resolve(name+EXTENSION);
    }

    @Override
    public Channel createChannel(String name, User owner) {
        Channel channel = new Channel(name, owner);
        Path path = resolvePath(name);
        if(Files.exists(path)){
           return null;
        }
        try(
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ){
            oos.writeObject(channel);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public Channel findChannel(String name) {
        Path path = resolvePath(name);
        if(Files.notExists(path)){
            return null;
        }
        try(
                FileInputStream fis = new  FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ){
            return (Channel) ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Channel changeChannel(Channel channel, String name, User requester) {
        Path oldpath = resolvePath(channel.getName());
        if (Files.notExists(oldpath)) {
            System.out.println("파일 없음.");
            return null;
        }

        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(oldpath.toFile()))) {
            Channel saveChannel = (Channel) ois.readObject();


        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String oldName = channel.getName();
        channel.update(name);
        Path newpath = resolvePath(name);
        try {
            try (
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(newpath.toFile()))) {
                oos.writeObject(channel);
            }
            if (!oldName.equals(name)) {
                Files.delete(oldpath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return channel;
    }



    @Override
    public void addUser(Channel channel, User user) {
        Path path = resolvePath(channel.getName());
        if(!path.toFile().exists()){
            throw new RuntimeException("not exists");
        }
        try(
                ObjectInputStream ois =new ObjectInputStream(new FileInputStream(path.toFile()))
        ){
            Channel channel1 = (Channel) ois.readObject();
            channel1.getMembers().add(user);
            this.changeChannel(channel1, channel.getName(), user);
            System.out.println(user.getUsername() + "이(가)" + channel.getName()+"에 초대되었습니다");
        }catch (IOException | ClassNotFoundException e){
            throw new RuntimeException(e);
        }






    }

    @Override
    public List<Channel> AllChannels() {
        try{
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path->{
                        try(
                                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))
                        ){
                            return (Channel) ois.readObject();
                        }catch (IOException | ClassNotFoundException e){
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean channelRemove(Channel channel, User requester) {
        Path path =resolvePath(channel.getName());
        if(!path.toFile().exists()){
            return false;
        }try{
            Files.delete(path);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return true;
    }
}


