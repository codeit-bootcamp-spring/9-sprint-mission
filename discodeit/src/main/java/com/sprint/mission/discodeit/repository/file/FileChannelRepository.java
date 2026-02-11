package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;     //경로
    private final String EXTENSION = ".ser";  //직렬화 약자

    public FileChannelRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }
/* 현재 프로젝트 경로안에 "file-data-map" 라는 폴더를 만들고 그리고 Channel class 이름(Channel)만 가져와서 경로를 만든다
지정된 경로에 폴더가 존재하지 않는가? true 실제 경로대로 폴더를 생성한다.
만약 아니면 입출력 에러가 날 수 있다
특정 채널의 uuid를 입력받아서 DIRECTORY 경로에 /id.ser붙인서 리턴값으로 보낸다 그리고 기존에 만든 저장경로에 방금만든 파일을 하나의 경로로 합친다
*/
    @Override
    public Channel save(Channel channel) {
        Path path = resolvePath(channel.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }
/* Resolvepath 메서드의 매개변수로 채널id 넣고 현재 프로젝트 경로/file-data-map/Channel에 id.ser을 합쳐서 path에 저장한다
FileOutputStream은 이 프로젝트와 컴퓨터를 연결해서 path를 파일 형태로 바꾸고
ObjectOutputStream으로 해당 파일을 byte형식으로 변환해서 저장한다
만약 이 코드가 실행되는 중에 입출력하는 과정에서 오류가 발생할시 런타임 오류를 발생시킨다
다 실행된 후 리턴값으로 채널을 반환한다
 */

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channelNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                channelNullable = (Channel) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(channelNullable);
    }
/* Channel 타입의 channelNullable에 null을 대입하고 resolvePath메서드의 매개변수로 채널 id를 넣는다
만약 저 경로가 존재할경우 FileOutputStream은 이 프로젝트와 컴퓨터를 연결해서 path를 파일 형태로 바꾸고
ObjectOutputStream으로 byte 형식의 파일을 Channel 타입으로 변환하여 channelNullable에 대입한다
만약 이 코드가 실행되는 중에 입출력하거나 클래스 없음 오류가 발생할시 런타임 오류를 발생시킨다
다 실행된 후 Optional.ofNullable 명령어로 리턴값이 null이여도 오류가 발생하지 않게 channelNullable변수를 반환한다.
 */
    @Override
    public List<Channel> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION)) //endsWith 문자열 형식으로 되어야 가능
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
/* 디렉토리안에 모든 파일을 List형식으로 가져온다 그리고 필터링을 통해 파일이 .ser로 끝나는 파일만 골라서 가져온다
가져온 파일들을 하나하나 FileOutputStream을 통해 이 프로젝트와 컴퓨터를 연결해서 path를 파일 형태로 바꾸고
ObjectOutputStream으로 byte 형식의 파일을 Channel 타입으로 변환하여 Channel 객체를 담는 리스트에 담아 리턴한다
만약 이 코드가 실행되는 중에 입출력하거나 클래스 없음 오류가 발생할시 런타임 오류를 발생시킨다
 */

    @Override
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }
//Resolvepath 메서드의 매개변수로 채널id 값을 넣고 그 리턴값을 path에 저장해서 path 경로의 존재여부를 반환한다

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
//삭제하고 싶은 채널 아이디를 입력 받아서 resolvePath 메서드에 매개변수로
//담아 결과 값을 path에 저장하고 path에 있는 파일을 삭제한다 파일이 이미 없을경우 에러가 발생한다