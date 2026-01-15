import entity.Channel;
import entity.Message;
import entity.User;
import service.ChannelService;
import service.MessageService;
import service.UserService;
import service.jcf.JCFChannelService;
import service.jcf.JCFMessageService;
import service.jcf.JCFUserService;
import java.util.List;
import java.util.Scanner;

public class JavaApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        User user = new User("전승현", "asdf@gmail.com", "01212301230");
        User user1 = new User("전팝콘", "asd", "213213");
        User user2 = new User("엘리스", "ㅁㄴㅇ", "010203020");

        UserService service = new JCFUserService();
        MessageService messageservice = new JCFMessageService();
        ChannelService channelservice = new JCFChannelService();

        User loginUser = null;
        boolean choice = true;
        int menu;

        while (choice) {
            System.out.println("\n ==== [ 메신저 시스템 메뉴 ] ====");
            System.out.println("0. 유저 초기 데이터 등록");
            System.out.println("---------------------------");
            System.out.println("1. [유저] 로그인");
            System.out.println("2. [유저] 단건 조회");
            System.out.println("3. [유저] 전체 조회");
            System.out.println("4. [유저] 정보 수정");
            System.out.println("5. [유저] 회원 삭제");
            System.out.println("---------------------------");
            System.out.println("6. [채널] 채널 생성");
            System.out.println("7. [채널] 채널명 수정 (방장권한)");
            System.out.println("8. [채널] 유저 초대");
            System.out.println("9. [채널] 모든 채널 출력");
            System.out.println("10. [채널] 채널 삭제 (방장권한)");
            System.out.println("---------------------------");
            System.out.println("11. [메시지] 메시지 작성");
            System.out.println("12. [메시지] 받은 메시지 확인");
            System.out.println("13. [메시지] 메시지 삭제");
            System.out.println("---------------------------");
            System.out.println("14. 로그아웃");
            System.out.println("15. 프로그램 종료");
            System.out.print("선택: ");

            try {
                menu = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력하세요.");
                continue;
            }

            switch (menu) {
                case 0:
                    service.addUser(user);
                    service.addUser(user1);
                    service.addUser(user2);
                    System.out.println("기본 유저 등록 완료");
                    break;

                case 1:
                    System.out.println("로그인 할 사용자 이름 입력:");
                    String userName = sc.nextLine().trim();
                    loginUser = service.getUser(userName);
                    if (loginUser != null) System.out.println("로그인 성공: " + loginUser.getUsername());
                    else System.out.println("존재하지 않는 사용자입니다.");
                    break;

                case 2:
                    System.out.println("조회할 이름 입력: ");
                    User foundUser = service.getUser(sc.nextLine().trim());
                    if (foundUser != null) System.out.println("회원정보: " + foundUser);
                    else System.out.println("회원이 없습니다.");
                    break;

                case 3:
                    if(notlogin(loginUser)) break;
                    System.out.println("==== 전체 회원 목록 ====");
                    service.getAllUsers().forEach(System.out::println);
                    break;

                case 4:
                    if(notlogin(loginUser)) break;
                    System.out.println("수정할 대상의 이름을 입력하시오: ");
                    User target = service.getUser(sc.nextLine().trim());
                    if (target != null) {
                        System.out.println("새 이름/이메일/번호 순차 입력: ");
                        target.update(sc.nextLine(), sc.nextLine(), sc.nextLine());
                        System.out.println("성공적으로 수정");
                    } else System.out.println("회원이 없습니다.");
                    break;

                case 5:
                    if(notlogin(loginUser)) break;
                    System.out.println("삭제할 회원의 이름을 입력하시오: ");
                    String dName = sc.nextLine().trim();
                    System.out.println("정말 삭제하시겠습니까? (y/n)");
                    if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                        if (service.deleteUser(dName)) System.out.println("유저 삭제 완료");
                        else System.out.println("유저가 없음");
                    }
                    break;

                case 6:
                    if(notlogin(loginUser)) break;
                    System.out.println("채널명을 입력하시오: ");
                    channelservice.createChannel(sc.nextLine().trim(), loginUser);
                    break;

                case 7:
                    if(notlogin(loginUser)) break;
                    System.out.println("변경할 채널 명을 입력하시오:");
                    Channel channel = channelservice.findChannel(sc.nextLine().trim());
                    if (channel != null) {
                        System.out.println("새로운 채널명 입력: ");
                        String newName = sc.nextLine().trim();
                        channelservice.changeChannel(channel, newName, loginUser);
                        if (channelservice.findChannel(newName) != null) {
                            System.out.println("이미 존재하는 이름입니다.");
                        }
                    } else System.out.println("채널을 찾을 수 없음");
                    break;

                case 8:
                    if(notlogin(loginUser)) break;
                    System.out.println("추가하실 멤버의 이름: ");
                    User targetUser1 = service.getUser(sc.nextLine().trim());
                    System.out.println("추가할 채널명: ");
                    Channel channel1 = channelservice.findChannel(sc.nextLine().trim());
                    if (targetUser1 != null && channel1 != null) {
                        channelservice.addUser(channel1, targetUser1);
                        System.out.printf("%s에 %s가 추가되었습니다.\n", channel1.getName(), targetUser1.getUsername());
                    } else System.out.println("유저 또는 채널 정보가 없습니다.");
                    break;

                case 9:
                    if(notlogin(loginUser)) break;
                    channelservice.AllChannels().forEach(System.out::println);
                    break;

                case 10:
                    if(notlogin(loginUser)) break;
                    System.out.println("삭제할 체널명을 입력하시오: ");
                    Channel targetChannel = channelservice.findChannel(sc.nextLine().trim());
                    if(targetChannel != null) {
                        System.out.println("정말로 삭제하시겠습니까?(y/n): ");
                        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                            if (channelservice.channelRemove(targetChannel, loginUser)) System.out.println("삭제 완료");
                            else System.out.println("방장이 아닙니다.");
                        }
                    } else System.out.println("채널을 찾을 수 없음");
                    break;

                case 11:
                    if(notlogin(loginUser)) break;
                    System.out.println("보낼 내용: ");
                    String content = sc.nextLine().trim();
                    System.out.println("받는 사람 이름: ");
                    User receiver = service.getUser(sc.nextLine().trim());
                    if (receiver != null) {
                        messageservice.sendMessage(new Message(content, loginUser, receiver));
                        System.out.println("메시지 전송 완료");
                    } else System.out.println("회원이 없습니다.");
                    break;

                case 12:
                    if(notlogin(loginUser)) break;
                    messageservice.getReceiverMessages(loginUser).forEach(System.out::println);
                    break;

                case 13:
                    if(notlogin(loginUser)) break;
                    System.out.println("삭제할 메시지 내용 입력: ");
                    String msgContent = sc.nextLine().trim();
                    System.out.println("정말 삭제하시겠습니까? (y/n)");
                    if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                        if (messageservice.deleteMessage(msgContent)) System.out.println("삭제 완료");
                        else System.out.println("메시지가 없음");
                    }
                    break;

                case 14:
                    if(notlogin(loginUser)) break;
                    System.out.println(loginUser.getUsername() + " 로그아웃됨");
                    loginUser = null;
                    break;

                case 15:
                    System.out.println("프로그램을 종료합니다.");
                    choice = false;
                    break;

                default:
                    System.out.println("잘못된 번호입니다.");
            }
        }
    }

    private static boolean notlogin(User loginUser) {
        if (loginUser == null) {
            System.out.println("로그인 하세요");
            return true;
        }
        return false;
    }
}