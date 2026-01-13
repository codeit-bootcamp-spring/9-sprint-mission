import entity.Channel;
import entity.Message;
import entity.User;
import service.ChannelService;
import service.MessageService;
import service.UserService;
import service.jcf.JCFChannelService;
import service.jcf.JCFMessageService;
import service.jcf.JCFUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;


public class JavaApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        User user = new User("전승현", "asdf@gmail.com", "01212301230");
        User user1 = new User("전팝콘", "asd", "213213");

        UserService service = new JCFUserService();
        MessageService messageservice = new JCFMessageService();
        ChannelService channelservice = new JCFChannelService();
        User loginUser = null;
        boolean choice = true;

        while (choice) {
            System.out.println("\n ==== 메뉴 ====");
            System.out.println("0.유저 등록");
            System.out.println("1.로그인");
            System.out.println("2.유저 조회(단건)");
            System.out.println("3.유저 조회(다건)");
            System.out.println("4.유저 수정");
            System.out.println("5.유저 삭제");
            System.out.println("6.메시지 작성 ");
            System.out.println("7.받은 메시지 확인 ");
            System.out.println("8.메시지 삭제");
            System.out.println("9.채널 생성");
            System.out.println("10.체널명 수정");
            System.out.println("11.유저 추가");
            System.out.println("12.모든 채널 출력");
            System.out.println("13.채널 삭제");
            System.out.println("14.로그아웃");
            System.out.println("15.프로그램 종료");
            System.out.println("선택: ");
            int menu = sc.nextInt();
            sc.nextLine();


            switch (menu) {
                case 0:
                    service.addUser(user);
                    service.addUser(user1);

                    break;
                case 1:
                    System.out.println("로그인 할 사용자 이름 입력:");
                    String username = sc.nextLine().trim();
                    loginUser = service.getUser(username);
                    if (loginUser != null) {
                        System.out.println("로그인 성공: " + loginUser.getUsername());
                    } else {
                        System.out.println("존재하지않음");
                    }
                    break;
                case 2:
                    System.out.println("회원 이름을 입력하시오.: ");
                    String searchName = sc.nextLine().trim();

                    User foundUser = service.getUser(searchName);
                    if (foundUser != null) {
                        System.out.println("회원: " + foundUser);
                    } else {
                        System.out.println("회원이 없습니다.");
                    }

                    break;
                case 3:
                    if (loginUser == null) {
                        System.out.println("로그인하세요");
                        break;
                    }
                    List<User> users = service.getAllUsers();
                    System.out.println("==== 전체 회원 목록 ====");
                    users.stream()
                            .forEach(System.out::println);
                    break;
                case 4:
                    if (loginUser == null) {
                        System.out.println("로그인하세요");
                        break;
                    }
                    System.out.println("수정할 대상의 이름을 입력하시오: ");
                    String targetname= sc.nextLine().trim();

                    User target = service.getUser(targetname);
                    if (target != null) {
                        System.out.println("새로운 이름 입력: ");
                        String newName = sc.nextLine().trim();
                        System.out.println("새로운 이메일 입력:");
                        String newEmail = sc.nextLine();
                        System.out.println("새로운 전화번호 입력");
                        String newPhoneNumber = sc.nextLine();
                        target.update(newName, newEmail, newPhoneNumber);
                        System.out.println("성공적으로 수정");

                    } else {
                        System.out.println("회원이없음.");
                    }
                    break;
                case 5:
                    if (loginUser == null) {
                        System.out.println("로그인하세요");
                        break;
                    }
                    System.out.println("삭제할 회원의 이름을 입력하시오: ");
                    String deleteName = sc.nextLine().trim();
                    boolean Delete = service.deleteUser(deleteName);
                    if (Delete) {
                        System.out.println("성공적으로 삭제");
                    } else {
                        System.out.println("회원이 없음.");

                    }
                    break;
                case 6:
                    if (loginUser == null) {
                        System.out.println("로그인하세요");
                        break;
                    }
                    System.out.println("보낼 메시지를 입력하시오: ");
                    String sendmessage = sc.nextLine().trim();
                    System.out.println("보낼 사람의 이름 입력:");
                    String targetName = sc.nextLine().trim();
                    User targetuser = service.getUser(targetName);
                    if (targetuser == null) {
                        System.out.println("회원이없습니다.");
                        break;
                    }

                    Message message1 = new Message(sendmessage, loginUser, targetuser, UUID.randomUUID());
                    messageservice.sendMessage(message1);
                    System.out.printf("%s에게 %s라는 내용의 메시지를 보냇습니다.", targetuser.getUsername(), message1.getContent());
                    break;
                case 7:
                    if (loginUser == null) {
                        System.out.println("로그인하세요");
                        break;
                    }
                    messageservice.getReceiverMessages(loginUser).stream().forEach(System.out::println);
                    break;
                case 8:
                    if (loginUser == null) {
                        System.out.println("로그인하세요");
                        break;
                    }
                    System.out.println("삭제할 메시지를 입력하시오: ");
                    String removeMessage = sc.nextLine().trim();
                    boolean Message= messageservice.deleteMessage(removeMessage);
                    if (Message) {
                        System.out.println("메시지 삭제 완료");
                    }else{
                        System.out.println("메시지가 없음");
                    }
                    break;

                case 9:
                    if (loginUser == null) {
                        System.out.println("로그인하세요");
                        break;
                    }
                    System.out.println("채널명을 입력하시오: ");
                    String channelName = sc.nextLine().trim();
                    Channel newChannel = channelservice.createChannel(channelName, loginUser);
                    if (newChannel == null) {
                        System.out.println("중복됨");
                    } else {
                        System.out.println(newChannel.getName() + "채널 생성됨");
                    }
                    break;
                case 10:
                    if (loginUser == null) {
                        System.out.println("로그인하세요");
                        break;
                    }
                    System.out.println("변경할 채널 명을 입력하시오:");
                    String name = sc.nextLine().trim();
                    Channel channel = channelservice.findChannel(name);
                    if (channel == null) {
                        System.out.println("잘못 입력함");
                    } else {
                        System.out.println("현재 체널: " + channel.getName());

                        System.out.println("새로운 채널명을 입력하시오: ");
                        String newName = sc.nextLine().trim();

                        if (channelservice.findChannel(newName) != null) {
                            System.out.println("이미 존재하는 이름입니다.");
                        } else {
                            channelservice.changeChannel(channel, newName, loginUser);
                            System.out.println("변경 완료");
                        }


                    }
                    break;
                case 11:
                    if (loginUser == null) {
                        System.out.println("로그인하세요");
                        break;
                    }
                    System.out.println("추가하실 멤버의 이름: ");
                    User targetUser = service.getUser(sc.nextLine().trim());
                    if(targetUser ==null){
                        System.out.println("회원이 없습니다");
                        break;
                    }

                    System.out.println("체널: ");
                    Channel channel1 = channelservice.findChannel(sc.nextLine().trim());
                    if(channel1 ==null){
                        System.out.println("채널이 없습니다.");
                        break;
                    }
                    channelservice.addUser(channel1,targetUser);
                    System.out.printf("%s에 %s가 정상적으로 추가되었습니다." ,channel1.getName(), targetUser.getUsername());
                    break;


                case 12:
                    if (loginUser == null) {
                        System.out.println("로그인하세요");
                        break;
                    }
                    channelservice.AllChannels().stream().forEach(System.out::println);
                    break;

                case 13:
                    if (loginUser == null) {
                        System.out.println("로그인하세요");
                        break;
                    }
                    System.out.println("삭제할 채널 명을 입력하시오: ");
                    String channelname = sc.nextLine().trim();

                    boolean removeChannel = channelservice.channelRemove(loginUser,channelname);

                    if (removeChannel) {
                        System.out.println("채널 삭제 완료");

                    }else{
                        System.out.println("채널이 없습니다.");
                    }
                    break;
                case 14:
                    if (loginUser == null) {
                        System.out.println("로그인 상태가 아닙니다.");
                    }else{
                        System.out.println(loginUser.getUsername()+": -로그아웃됌");
                        loginUser=null;
                    }
                    break;
                case 15:
                    System.out.println("프로그램을 종료합니다.");
                    choice=false;
                    break;





            }

        }


    }
}


