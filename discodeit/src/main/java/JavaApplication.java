import entity.Channel;
import entity.ChannelMessage;
import entity.Message;
import entity.User;
import service.ChannelMessageService;
import service.ChannelService;
import service.MessageService;
import service.UserService;
import service.jcf.JCFChannelMessageService;
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
        ChannelMessageService channelmessageservice = new JCFChannelMessageService();

        User loginUser = null;
        boolean choice = true;

        while (choice) {
            System.out.println("\n========= [ 메인 메뉴 ] =========");
            System.out.println("1. 유저 관리   2. 채널 관리   3. DM 관리");
            System.out.println("4.그룹 채팅 관리 5. 로그아웃    6. 프로그램 종료");
            System.out.print("선택: ");

            int mainMenu;
            try {
                mainMenu = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("숫자를 입력해주세요.");
                continue;
            }

            switch (mainMenu) {
                case 1:
                    boolean userMode = true;
                    while (userMode) {
                        System.out.println("\n--- [ 유저 관리 모드 ] ---");
                        System.out.println("1. 초기 등록   2. 로그인     3. 단건 조회");
                        System.out.println("4. 전체 조회   5. 정보 수정   6. 회원 탈퇴");
                        System.out.println("0. 메인 메뉴로 이동 (혹은 q 입력)");
                        System.out.print("기능 선택: ");

                        String input = sc.nextLine().trim();
                        if (input.equalsIgnoreCase("q") || input.equals("0")) {
                            userMode = false;
                            continue;
                        }

                        int userAction;
                        try {
                            userAction = Integer.parseInt(input);
                        } catch (Exception e) {
                            System.out.println(" 잘못된 입력입니다. 유저 관리 모드를 유지합니다.");
                            continue;
                        }

                        switch (userAction) {
                            case 1:
                                service.addUser(user); service.addUser(user1); service.addUser(user2);
                                System.out.println("기본 유저 등록 완료");
                                break;
                            case 2:
                                System.out.println("로그인 할 이름:");
                                loginUser = service.getUser(sc.nextLine().trim());
                                if (loginUser != null) System.out.println("성공: " + loginUser.getUsername());
                                else System.out.println("실패: 유저 없음");
                                break;
                            case 3:
                                if(notlogin(loginUser))break;
                                System.out.println("조회할 이름:");
                                User found = service.getUser(sc.nextLine().trim());
                                System.out.println(found != null ? found : "회원 없음");
                                break;
                            case 4:
                                if(notlogin(loginUser)) break;
                                service.getAllUsers().forEach(System.out::println);
                                break;
                            case 5:
                                if(notlogin(loginUser)) break;
                                System.out.println("수정 대상 이름:");
                                User t = service.getUser(sc.nextLine().trim());
                                if (t != null) {
                                    System.out.println("새 이름/이메일/번호 입력:");
                                    t.update(sc.nextLine(), sc.nextLine(), sc.nextLine());
                                    System.out.println("수정 완료");
                                }
                                break;
                            case 6:
                                if(notlogin(loginUser)) break;
                                System.out.println("삭제 대상 이름:");
                                if (service.deleteUser(sc.nextLine().trim())) System.out.println("삭제 성공");
                                else System.out.println("삭제 실패");
                                break;
                            default:
                                System.out.println("존재하지 않는 번호입니다.");
                        }
                    }
                    break;

                case 2:

                    boolean channelMode = true;
                    while (channelMode) {
                        System.out.println("\n--- [ 채널 관리 모드 ] ---");
                        System.out.println("1. 채널 생성   2. 채널명 수정  3. 유저 초대");
                        System.out.println("4. 채널 목록   5. 채널 삭제    0. 메인 메뉴로 이동 (혹은 q 입력)");
                        System.out.print("기능 선택: ");

                        String input = sc.nextLine().trim();
                        if (input.equalsIgnoreCase("q") || input.equals("0")) {
                            channelMode = false;
                            continue;
                        }

                        int channelAction;
                        try {
                            channelAction = Integer.parseInt(input);
                        } catch (Exception e) {
                            System.out.println(" 잘못된 입력입니다. 채널 관리 모드를 유지합니다.");
                            continue;
                        }

                        switch (channelAction) {
                            case 1:
                                System.out.println("채널명:");
                                channelservice.createChannel(sc.nextLine().trim(), loginUser);
                                break;
                            case 2:
                                System.out.println("대상 채널명:");
                                Channel c = channelservice.findChannel(sc.nextLine().trim());
                                if (c != null) {
                                    System.out.println("새 채널명:");
                                    channelservice.changeChannel(c, sc.nextLine().trim(), loginUser);


                                }
                                break;
                            case 3:
                                System.out.println("초대할 이름:");
                                User targetU = service.getUser(sc.nextLine().trim());
                                System.out.println("채널명:");
                                Channel targetC = channelservice.findChannel(sc.nextLine().trim());
                                if (targetU != null && targetC != null) {
                                    channelservice.addUser(targetC, targetU);
                                    System.out.println("초대 완료");
                                }
                                break;
                            case 4:
                                channelservice.AllChannels().forEach(System.out::println);
                                break;
                            case 5:
                                System.out.println("삭제할 채널명:");
                                Channel delC = channelservice.findChannel(sc.nextLine().trim());
                                if (channelservice.channelRemove(delC, loginUser)) System.out.println("삭제 성공");
                                else System.out.println("삭제 실패");
                                break;
                            default:
                                System.out.println(" 존재하지 않는 번호입니다.");
                        }
                    }
                    break;

                case 3:
                    if(notlogin(loginUser)) break;
                    boolean dmMode = true;
                    while (dmMode) {
                        System.out.println("\n--- [ DM 관리 모드 ] ---");
                        System.out.println("1. DM 전송    2. 받은 DM 확인 3. DM 삭제");
                        System.out.println("0. 메인 메뉴로 이동 (혹은 q 입력)");
                        System.out.print("기능 선택: ");

                        String input = sc.nextLine().trim();
                        if (input.equalsIgnoreCase("q") || input.equals("0")) {
                            dmMode = false;
                            continue;
                        }

                        int dmAction;
                        try {
                            dmAction = Integer.parseInt(input);
                        } catch (Exception e) {
                            System.out.println("잘못된 입력입니다. DM 관리 모드를 유지합니다.");
                            continue;
                        }

                        switch (dmAction) {
                            case 1:
                                System.out.println("받는 사람:");
                                User recv = service.getUser(sc.nextLine().trim());
                                if (recv != null) {
                                    System.out.println("내용:");
                                    messageservice.sendMessage(new Message(sc.nextLine(), loginUser, recv));
                                    System.out.println("전송 완료");
                                }else{
                                    System.out.println("회원이 없습니다.");
                                }
                                break;
                            case 2:
                                messageservice.getReceiverMessages(loginUser).forEach(System.out::println);
                                break;
                            case 3:
                                System.out.println("삭제할 내용:");
                                String message = sc.nextLine().trim();
                                boolean delete = messageservice.deleteMessage(message, loginUser);
                                if (delete) {
                                    System.out.println("삭제 완료");
                                }else{
                                    System.out.println("권한이 없거나 메시지가 존재하지않는다.");
                                }
                                break;
                            default:
                                System.out.println(" 존재하지 않는 번호입니다.");
                        }
                    }
                    break;

                case 4:
                    if(notlogin(loginUser)) break;
                    boolean groupMode = true;
                    while (groupMode) {
                        System.out.println("\n ==== 그룹 채팅 관리 ====");
                        System.out.println("1. 그룹에 메세지 보내기  2.그룹 메세지 조회 3.메세지 삭제");
                        System.out.println("0. 메인 메뉴로 이동 (혹은 q 입력)");
                        System.out.println("기능 선택:");
                        String input = sc.nextLine().trim();
                        if (input.equalsIgnoreCase("q") || input.equals("0")) {
                            groupMode = false;
                            continue;
                        }

                        int groupAction;
                        try {
                            groupAction = Integer.parseInt(input);
                        } catch (Exception e) {
                            System.out.println(" 잘못된 입력입니다. group 관리 모드를 유지합니다.");
                            continue;
                        }
                        switch (groupAction) {
                            case 1:
                                System.out.println("채널명을 입력하시오:");
                                String choiceName= sc.nextLine().trim();
                                Channel currentChannel = channelservice.findChannel(choiceName);
                                if(currentChannel == null){
                                    System.out.println("채널이 없습니다");
                                    break;
                                }
                                System.out.println(currentChannel.getName()+"채널에 접속했습니다.");
                                System.out.println("보낼 메세지를 입력하시오: ");
                                String message = sc.nextLine().trim();
                                ChannelMessage newMessage = new ChannelMessage(message,loginUser,currentChannel);
                                channelmessageservice.sendMessage(loginUser,newMessage,currentChannel);
                                System.out.println("메시지 전송 완료");
                                break;
                            case 2:
                                System.out.println("채널명을 입력하시오: ");
                                String searchName =sc.nextLine().trim();
                                Channel channel = channelservice.findChannel(searchName);
                                if(channel == null){
                                    System.out.println("채널이 없음");
                                    break;
                                }
                                List<ChannelMessage> messages = channelmessageservice.getChannelMessages(channel,loginUser);
                                if(messages.isEmpty()){
                                    System.out.println("메시지가없어요");
                                }else{
                                    System.out.println(channel.getName()+"대화 내역입니다.");
                                    messages.forEach(m->System.out.println(m.getSender().getUsername()+ ":" + m.getContent()));
                                }
                                break;
                            case 3:
                                System.out.println("채널명을 입력하시오: ");
                                String Name =sc.nextLine().trim();
                                Channel channel2 = channelservice.findChannel(Name);
                                if(channel2 == null){
                                    System.out.println("채널이 없음");
                                    break;

                                }

                                System.out.println("삭제할 메시지를 입력하시오: ");
                                String targetContent = sc.nextLine().trim();
                                ChannelMessage deleteMessage = new ChannelMessage(targetContent,loginUser,channel2);
                                boolean Delete = channelmessageservice.deleteMessage(loginUser,deleteMessage,channel2);
                                if(Delete){
                                    System.out.println("메시지 삭제완료");
                                }else{
                                    System.out.println("권한이 없거나 메시지가 없음");
                                }





                                break;






                        }


                    }
                    break;

                case 5:
                    if (loginUser != null) {
                        System.out.println(loginUser.getUsername() + " 로그아웃됨");
                        loginUser = null;
                    }
                    break;

                case 6:
                    System.out.println("프로그램 종료");
                    choice = false;
                    break;
            }


        }


    }

    private static boolean notlogin(User loginUser) {
        if (loginUser == null) {
            System.out.println("로그인이 필요합니다.");
            return true;
        }
        return false;
    }
}