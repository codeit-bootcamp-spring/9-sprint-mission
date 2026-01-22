package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.nio.channels.IllegalChannelGroupException;
import java.util.List;
import java.util.Scanner;

public class JavaApplication {

    // 전역 변수: 현재 로그인한 유저와 접속 중인 채널
    static User currentUser = null;
    static Channel currentChannel = null;

    // 서비스 및 스캐너
    static UserService userService = new JCFUserService();
    static ChannelService channelService = new JCFChannelService();
    static MessageService messageService = new JCFMessageService();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   🚀 DISCODEIT CONSOLE APP STARTED 🚀   ");
        System.out.print("==========================================");

        boolean running = true;
        while (running) {
            printMenu();
            String input = scanner.nextLine();

            try {
                switch (input) {
                    // --- 유저 관리 ---
                    case "1": registerUser(); break;
                    case "2": loginProcess(); break; // (임시) 목록에서 유저 선택해서 로그인
                    case "3": viewMyProfile(); break;
                    case "4": updateNickname(); break;
                    case "5": updatePassword(); break;
                    case "6": updateStatus(); break;
                    case "7": deleteMyAccount(); break;

                    // --- 채널 관리 ---
                    case "8": listChannels(); break;
                    case "9": createChannel(); break;
                    case "10": enterChannel(); break; // 채널 입장
                    case "11": updateChannelInfo(); break;
                    case "12": deleteChannel(); break;

                    // --- 메시지 관리 (채널에 들어왔을 때만) ---
                    case "13": writeMessage(); break;
                    case "14": updateMessage(); break;
                    case "15": listMessages(); break;
                    case "16": deleteMessage(); break;
                    case "99": logout(); break;

                    case "0":
                        running = false;
                        System.out.println("👋 프로그램을 종료합니다.");
                        break;
                    default:
                        System.out.println("❌ 잘못된 입력입니다.");
                }
            } catch (Exception e) {
                System.out.println("❌ 오류 발생: " + e.getMessage());
            }
        }
        scanner.close();
    }

    // --- 메뉴 출력 ---
    static void printMenu() {
        System.out.println("\n------------------------------------------");
        if (currentUser != null) {
            System.out.print("👤 내 계정: " + currentUser.getNickname() + " (" + currentUser.getStatus() + ")");
            if (currentChannel != null) {
                System.out.print(" | 📢 현재 채널: [" + currentChannel.getChannelName() + "]");
            }
            System.out.println();
        } else {
            System.out.println("👤 로그인 필요 (비회원 상태)");
        }
        System.out.println("------------------------------------------");

        if (currentUser == null) {
            System.out.println("[1] 회원가입  [2] 로그인(유저선택)");
        } else {
            System.out.println("--- 👤 유저 설정 ---");
            System.out.println("[3] 내 정보 조회  [4] 닉네임 변경  [5] 비번 변경");
            System.out.println("[6] 상태 변경    [7] 회원 탈퇴    [99] 로그아웃");

            System.out.println("\n--- 📺 채널 관리 ---");
            System.out.println("[8] 채널 목록    [9] 채널 생성    [10] 채널 입장(선택)");
            System.out.println("[11] 채널 수정   [12] 채널 삭제");

            if (currentChannel != null) {
                System.out.println("\n--- 💬 [" + currentChannel.getChannelName() + "] 메시지 ---");
                System.out.println("[13] 메시지 쓰기 [14] 메시지 수정 [15] 메시지 보기 [16] 메시지 삭제");
            }
        }
        System.out.println("[0] 종료");
        System.out.print("명령어 입력 > ");
    }

    // --- 1. 유저 기능 구현 ---

    static void registerUser() {
        try {
            System.out.print("이메일: "); String email = scanner.nextLine().trim();
            System.out.print("닉네임: "); String nick = scanner.nextLine().trim();
            System.out.print("비밀번호: "); String pass = scanner.nextLine().trim();

            // 여기서 예외가 발생하면 바로 catch 블록으로 점프합니다.
            User user = new User(email, nick, pass);

            currentUser = userService.createUser(user);
            System.out.println("✅ 회원가입 및 로그인 완료!");

        } catch (IllegalArgumentException e) {
            // 생성자에서 던진 메시지("이메일을 비워둘 수 없습니다." 등)를 출력합니다.
            System.out.println("❌ 입력 오류: " + e.getMessage());
            System.out.println("회원가입 단계로 돌아갑니다.");
        }
    }

    static void loginProcess() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("❌ 등록된 유저가 없습니다. 회원가입부터 해주세요.");
            return;
        }
        System.out.println("--- 유저 목록 ---");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getNickname());
        }
        System.out.print("로그인할 유저 번호 선택: ");
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        if (idx >= 0 && idx < users.size()) {
            currentUser = users.get(idx);
            System.out.println("✅ " + currentUser.getNickname() + "님으로 로그인되었습니다.");
        }
    }

    static void viewMyProfile() {
        if (currentUser == null) return;
        System.out.println("--- 내 정보 ---");
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Nickname: " + currentUser.getNickname());
        System.out.println("Status: " + currentUser.getStatus());
    }

    static void updateNickname() {
        if (currentUser == null) return;
        System.out.print("새로운 닉네임: ");
        String newNick = scanner.nextLine().trim();

        // JCFUserService에 넘길 임시 객체 (비밀번호는 null)
        User temp = new User(currentUser.getEmail(), newNick, currentUser.getPassword());
        currentUser = userService.updateUser(currentUser.getId(), temp);
        System.out.println("✅ 닉네임 변경 완료!");
    }

    static void updatePassword() {
        if (currentUser == null) return;
        System.out.print("새로운 비밀번호: ");
        String newPass = scanner.nextLine();

        // JCFUserService에 넘길 임시 객체 (닉네임은 null)
        User temp = new User(currentUser.getEmail(), currentUser.getNickname(), newPass);
        currentUser = userService.updateUser(currentUser.getId(), temp);
        System.out.println("✅ 비밀번호 변경 완료!");
    }

    static void updateStatus() {
        try{
            if (currentUser == null) return;
            System.out.println("1. ONLINE  2. OFFLINE  3. AWAY  4. DO_NOT_DISTURB");
            System.out.print("상태 번호 선택: ");
            String sel = scanner.nextLine();
            UserStatus status = switch (sel) {
                case "1" -> UserStatus.ONLINE;
                case "2" -> UserStatus.OFFLINE;
                case "3" -> UserStatus.AWAY;
                case "4" -> UserStatus.DO_NOT_DISTURB;
                default -> null;
            };
            // Entity의 setStatus 직접 호출 (메모리 방식이라 반영됨)
            currentUser.setStatus(status);
            System.out.println("✅ 상태 변경 완료: " + status);
        }catch (IllegalArgumentException e){
            System.out.println("❌ 입력 오류: " + e.getMessage());
            System.out.println("메뉴로 돌아갑니다.");
        }

    }

    static void deleteMyAccount() {
        if (currentUser == null) return;
        System.out.print("정말 탈퇴하시겠습니까? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            userService.deleteUser(currentUser.getId());
            currentUser = null;
            currentChannel = null;
            System.out.println("🗑 계정이 삭제되었습니다.");
        }
    }
    static void logout() {
        if (currentUser == null) {
            System.out.println("❌ 이미 비회원 상태입니다.");
            return;
        }
        System.out.println("👋 " + currentUser.getNickname() + "님, 로그아웃 되었습니다.");

        // 핵심: 로그인 정보를 지우고, 채널에서도 나감
        currentUser = null;
        currentChannel = null;
    }

    // --- 2. 채널 기능 구현 ---

    static void listChannels() {
        List<Channel> channels = channelService.getAllChannels();
        System.out.println("\n--- 채널 목록 ---");
        if (channels.isEmpty()) System.out.println("(채널이 없습니다)");
        for (int i = 0; i < channels.size(); i++) {
            Channel ch = channels.get(i);
            System.out.println((i + 1) + ". [" + ch.getChannelName() + "] - " + ch.getDescription());
        }
    }

    static void createChannel() {
        try{
            if (currentUser == null) {
                System.out.println("❌ 로그인 후 가능합니다.");
                return;
            }
            System.out.print("채널 이름: ");
            String name = scanner.nextLine().trim();
            System.out.print("채널 설명 (선택): ");
            String desc = scanner.nextLine().trim();

            Channel ch = new Channel(name, desc);
            channelService.createChannel(ch);
            System.out.println("✅ 채널 생성 완료!");

        }catch (IllegalArgumentException e) {
            System.out.println("❌ 입력 오류: " + e.getMessage());
            System.out.println("메뉴로 돌아갑니다.");
        }
    }

    static void enterChannel() {
        if (currentUser == null) return;
        List<Channel> channels = channelService.getAllChannels();
        listChannels();
        if (channels.isEmpty()) return;

        System.out.print("입장할 채널 번호: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < channels.size()) {
                currentChannel = channels.get(idx);
                System.out.println("📢 [" + currentChannel.getChannelName() + "] 채널에 입장했습니다.");
            } else {
                System.out.println("❌ 잘못된 번호입니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 숫자를 입력해주세요.");
        }
    }

    static void updateChannelInfo() {
        if (currentChannel == null) { System.out.println("❌ 채널에 먼저 입장해주세요."); return; }
        System.out.print("새 채널 이름: "); String name = scanner.nextLine();
        System.out.print("새 설명: "); String desc = scanner.nextLine();

        Channel temp = new Channel(name, desc);
        // temp를 만들었지만 ID가 다르므로, service update 호출 시 기존 ID를 넘겨야 함
        currentChannel = channelService.updateChannel(currentChannel.getId(), temp);
        System.out.println("✅ 채널 정보 수정 완료!");
    }

    static void deleteChannel() {
        if (currentChannel == null) { System.out.println("❌ 채널에 먼저 입장해주세요."); return; }
        System.out.print("현재 채널을 삭제하시겠습니까? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            channelService.deleteChannel(currentChannel.getId());
            System.out.println("🗑 채널 [" + currentChannel.getChannelName() + "] 삭제 완료.");
            currentChannel = null; // 채널에서 쫓겨남
        }
    }

    // --- 3. 메시지 기능 구현 ---

    static void writeMessage() {
        try{
            if (currentUser == null || currentChannel == null) {
                System.out.println("❌ 로그인 및 채널 입장이 필요합니다."); return;
            }
            System.out.print("메시지 입력: ");
            String content = scanner.nextLine();
            Message msg = new Message(content, currentUser.getId(), currentChannel.getId());
            messageService.createMessage(msg);
            System.out.println("✅ 전송됨");
        }catch (IllegalChannelGroupException e){
            System.out.println("❌ 입력 오류: " + e.getMessage());
            System.out.println("메뉴로 돌아갑니다.");
        }
    }
    static void updateMessage() {
        try{
            if(currentChannel == null) return;

        }catch (IllegalArgumentException e){

        }
    }
    static void listMessages() {
        if (currentChannel == null) return;
        List<Message> allMsgs = messageService.getAllMessages();

        System.out.println("\n--- 💬 [" + currentChannel.getChannelName() + "] 채팅 로그 ---");
        boolean empty = true;

        // '현재 채널'의 메시지만 필터링해서 보여주기
        int displayIndex = 1;
        // 나중에 삭제할 때 쓰기 위해 ID를 저장할 수도 있지만, 여기선 단순히 보여주기만 함
        for (Message m : allMsgs) {
            if (m.getChannelId().equals(currentChannel.getId())) {
                // 작성자 닉네임 찾기 (ID로)
                User writer = userService.getUser(m.getUserId());
                String writerName = (writer != null) ? writer.getNickname() : "(알수없음)";

                System.out.println(displayIndex++ + ". [" + writerName + "] : " + m.getContent());
                empty = false;
            }
        }
        if (empty) System.out.println("(메시지가 없습니다)");
    }

    static void deleteMessage() {
        if (currentChannel == null) return;
        System.out.println("삭제 기능은 메시지 ID를 알아야 하므로, 이번 버전에서는 생략하거나 전체 삭제만 가능합니다.");
        // 심화: 메시지 목록을 띄울 때 List에 담아두고 인덱스로 삭제하는 로직 구현 가능
    }
}