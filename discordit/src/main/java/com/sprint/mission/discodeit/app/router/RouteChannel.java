package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.UpdateChannelDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RouteChannel {
    private final BasicChannelService channelService;
    private final IsLogin isLogin;
    private final Scanner scanner;
    private final BasicUserStatusService userStatusService;
    private final ReadStatusService readStatusService;
    private final BasicMessageService messageService;
    private final BinaryContentRepository binaryContentRepository;
    private final UserState userState;
    private final BasicUserService userService;

    public void route(int routeCRUD) {
        if(!isLogin.check("Channel", routeCRUD)) {
            System.err.println("해당 기능은 로그인한 후 이용 가능합니다.");
            return;
        }

        try {
            switch (routeCRUD) {

                /// create
                case 1:
                    create();
                    break;

                /// update
                case 2:
                    update();
                    break;

                /// read
                case 3:
                    read();
                    break;

                /// delete
                case 4:
                    delete();
                    break;

                /// invite Channel
                case 5:
                    invite();
                    break;

                default:
                    System.err.println("잘못된 입력값입니다.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] " + e);
        }
    }

    private void create() {
        System.out.println("사용하려는 채널명이 무엇인가요?");
        String name = scanner.nextLine().trim();

        if (channelService.isPresent(name)) {
            System.err.println("이미 존재하는 채널명입니다.");
            return;
        };

        System.out.println("해당 채널의 성격을 알려주세요. (숫자 혹은 뒤의 영어를 입력해주시면 됩니다.)");
        System.out.println("1. PUBLIC");
        System.out.println("2. PRIVATE");
        String type = scanner.nextLine().trim();
        String userName = userState.getUserName();

        if(!channelService.create(type, name, userName)) {
            System.err.println("잘못된 입력값입니다. 처음으로 돌아갑니다.");
            return;
        };

        UUID userId = userState.getUserId();
        UUID channelId = channelService.findChannelId(name);
        accessTimeUpdate();

        readStatusService.create(userId, channelId);

        System.out.println("성공");
    }

    private void update() {
        System.out.println("변경하고자 하는 채널명을 알려주세요");
        String oldName = scanner.nextLine();

        if (!channelService.isPresent(oldName)) {
            System.err.println("해당 채널이 존재하지 않습니다.");
            return;
        }

        System.out.println("현재 채널명 : " + oldName);
        System.out.println("무엇으로 변경하고 싶은가요? ");

        String newName = scanner.nextLine();

        try {
            if (channelService.update(new UpdateChannelDto(oldName, newName))) {
                System.out.println("잘 변경되었어요!");
                accessTimeUpdate();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] " + e);
        }
    }

    private void read() {
        String name;
        int menu;
        System.out.println("전체 사용자 정보를 가져올까요?");
        System.out.println("1 : 특정 채널만 가져옵니다");
        System.out.println("2 : 전체 채널을 가져옵니다");

        try {
            menu = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("잘못된 입력값입니다. : " + e);
            return;
        }

        if (menu == 1) {
            System.out.println("검색할 채널명을 알려주세요");
            name = scanner.nextLine().trim();
            channelService.find(name);
        }
        else if (menu == 2) {
            List<ResponseChannelDto> requestAllChannelDto = channelService.findAll(userState.getUserName());
            if(requestAllChannelDto.isEmpty()) {
                System.err.println("채널이 존재하지 않습니다.");
                return;
            }

            requestAllChannelDto.forEach(req -> {
                System.out.println(req);
                System.out.println("마지막 메시지 시간 : " + messageService.lastMessageTime(req.channelName()));
            });
            System.out.println("총 채널 수 : " + requestAllChannelDto.size());
            accessTimeUpdate();
        }
    }

    private void delete() {
        System.out.println("[Warning!] 지금 계정을 삭제하려 하고 있습니다.");
        System.out.println("[Warning!] 만약 잘못 들어오신 경우, 0을 눌러주시기 바랍니다.");
        System.out.println("[Warning!] 계속 진행하려면 아무 숫자나 입력해주세요");

        int n = scanner.nextInt();
        scanner.nextLine();

        if (n == 0) {
            System.out.println("처음으로 돌아갑니다.");
            return;
        }

        System.out.print("삭제하려는 채널명을 알려주세요: ");
        String name = scanner.nextLine();

        if (!channelService.isPresent(name)) {
            System.err.println("해당 채널을 찾을 수 없습니다.");
            return;
        }

        UUID channelId = channelService.findChannelId(name);

        if(channelService.delete(name)) {
            messageService.deleteAll(channelId);
            readStatusService.deleteForChannel(channelId);
            System.out.println("성공적으로 삭제되었습니다.");
        } else {
            System.err.println("삭제하지 못했어요!");
        }
    }

    private void invite() {
        System.out.println("현재 당신이 접속하고 있는 Private Channel은 다음과 같습니다.");

        try {
            channelService.findAllPrivateChannel(userState.getUserName()).forEach(channel -> {
                System.out.println("채널명 : " + channel.channelName());
            });
        } catch (Exception e) {
            System.err.println("[ERROR] " + e);
        }

        System.out.println("초대하고자 하는 채널명을 입력해주세요.");
        String channelName = scanner.nextLine().trim();

        if(!channelService.isPresent(channelName)) {
            System.err.println("해당 채널이 존재하지 않습니다.");
            return;
        }

        System.out.println("초대하고자 하는 사용자명을 입력해주세요.");
        String userName = scanner.nextLine().trim();

        if(!userService.isPresent(userName)) {
            System.err.println("해당 사용자가 존재하지 않습니다.");
            return;
        }

        UUID userId = userService.userNameToId(userName);

        channelService.includePrivateChannel(channelName, userName, userId);

        System.out.println("성공");
    }

    private void accessTimeUpdate() {
        String userName = userState.getUserName();
        UUID userId = userState.getUserId();
        userStatusService.update(new UserStatusUpdateDto(userId, userName, Instant.now()));
    }
}
