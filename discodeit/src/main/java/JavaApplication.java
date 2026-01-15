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
import java.util.UUID;


public class JavaApplication {
    static void userCRUDTest(UserService userService) {
        // 생성

            Scanner sc = new Scanner(System.in);
            boolean run = true;
            while (run) {
                System.out.println("\n==== 유저 관리 ====");
                System.out.println("1. 유저 등록");
                System.out.println("2. 유저 정보 조회");
                //정보 조회안에 단건,다건으로 분화
                System.out.println("3. 유저 정보 수정");
                System.out.println("4. 유저 삭제");
                System.out.println("5. 종료");
                System.out.println("선택: ");
                int menu = sc.nextInt();
                sc.nextLine();


                switch (menu) {
                    case 1:
                        System.out.println("\n==== 유저 등록 ====");
                        System.out.println("이름 입력: ");
                        String userName = sc.nextLine();
                        System.out.println("이메일: ");
                        String email = sc.nextLine();
                        System.out.println("전화번호: ");
                        String phoneNumber = sc.nextLine();
                        System.out.println();

                        User user = userService.create(userName, email, phoneNumber);
                        System.out.println("----[등록 성공]----");
                        System.out.println("유저 ID: " + user.getId());
                        System.out.println("이름: " + user.getuserName());
                        System.out.println("이메일: " + user.getemail());
                        System.out.println("전화번호: " + user.getphoneNumber());
                        System.out.println();
                        System.out.println("[Enter] 입력시 메뉴로 이동");
                        sc.nextLine();
                        break;

                    case 2:
                        System.out.println("\n==== 유저 정보 조회 ====");
                        System.out.println("1. 단건");
                        System.out.println("2. 다건");
                        System.out.println("선택");
                        int submenu = sc.nextInt();
                        sc.nextLine(); // nextInt로 끝나고 입력된 'Enter'를 읽어 버림 => " " 빈값을 읽고 오류
                        //switch 내부에서 다시 switch를 들어갈려면 시작전에 submenu를 추가로 초기화해야함
                        switch (submenu) {

                            case 1:
                                System.out.println("\n==== 유저 정보 단건 조회 ====");
                                System.out.println("유저 이름을 입력해주세요: ");
                                String foundName = sc.nextLine();
                                User foundUser = userService.findByName(foundName);

                                if (foundUser == null) {
                                    System.out.println("유저 정보 없음");
                                    System.out.println();
                                    System.out.println("[Enter] 입력시 메뉴로 이동");
                                    sc.nextLine();

                                    break;
                                } else {
                                    System.out.println("유저 ID: " + foundUser.getId());
                                    System.out.println("이름: " + foundUser.getuserName());
                                    System.out.println("이메일: " + foundUser.getemail());
                                    System.out.println("전화번호: " + foundUser.getphoneNumber());
                                    System.out.println();
                                    System.out.println("[Enter] 입력시 메뉴로 이동");
                                    sc.nextLine();

                                    break;
                                }

                            case 2:
                                System.out.println("\n==== 유저 정보 다건 조회 ====");
                                List<User> founders = userService.findAll();
                                // 이름을 정렬해주고 싶다 -> 리스트에서 하나씩 꺼내어 정렬
                                System.out.println("유저 수: " + founders.size());
                                System.out.println("유저 목록: ");
                                for (User u : founders) {
                                    System.out.println("- " + u.getuserName());
                                }
                                System.out.println();
                                System.out.println("[Enter] 입력시 메뉴로 이동");
                                sc.nextLine();
                                break;
                        }
                        break;

                    case 3://
                        System.out.println("\n==== 유저 정보 수정 ====");
                        System.out.println("ID 입력: ");
                        // UUID를 바로 대입 할 수 없어서 String값으로 변환
                        // try - catch 문을 사용해서 실패할 부분을 안전하게 만듦

                        UUID updateId;

                        try {
                            updateId = UUID.fromString(sc.nextLine());
                        } catch (IllegalArgumentException e) {
                            System.out.println("잘못된 ID 형식입니다.");
                            System.out.println("[Enter] 입력시 메뉴로 이동");
                            sc.nextLine();
                            break;
                        }
                        User targetUser = userService.find(updateId);
                        if (targetUser == null) {
                            System.out.println("없는 유저입니다");
                            System.out.println("[Enter] 입력시 메뉴로 이동");
                            sc.nextLine();
                            break;
                        } else {
                            System.out.println("이름: ");
                            String userName2 = sc.nextLine();
                            System.out.println("이메일: ");
                            String email2 = sc.nextLine();
                            System.out.println("전화번호: ");
                            String phoneNumber2 = sc.nextLine();
                            System.out.println();

                            User updateUser = userService.update(updateId, userName2, email2, phoneNumber2);
                            System.out.println("----[수정 완료]----");
                            System.out.println("이름: " + updateUser.getuserName());
                            System.out.println("이메일: " + updateUser.getemail());
                            System.out.println("전화번호: " + updateUser.getphoneNumber());
                            System.out.println();
                            System.out.println("[Enter] 입력시 메뉴로 이동");
                            sc.nextLine();


                            break;
                        }
                    case 4:
                        System.out.println("\n==== 유저 정보 삭제 ====");
                        System.out.println("ID 입력: ");
                        // UUID를 바로 대입 할 수 없어서 String값으로 변환
                        // try - catch 문을 사용해서 실패할 부분을 안전하게 만듦

                        UUID deleteId;

                        try {
                            deleteId = UUID.fromString(sc.nextLine());
                        } catch (IllegalArgumentException e) {
                            System.out.println("잘못된 ID 형식입니다.");
                            System.out.println("[Enter] 입력시 메뉴로 이동");
                            sc.nextLine();
                            break;
                        }
                        User targetUser2 = userService.find(deleteId);
                        if (targetUser2 == null) {
                            System.out.println("없는 유저입니다");
                            System.out.println("[Enter] 입력시 메뉴로 이동");
                            sc.nextLine();
                            break;
                        }
                        userService.delete(deleteId);
                        System.out.println();
                        System.out.println("----[삭제 완료]----");

                        System.out.println();
                        System.out.println("[Enter] 입력시 메뉴로 이동");
                        sc.nextLine();

                        break;

                    case 5:
                        run = false;
                        System.out.println("[프로그램 종료]");

                        break;
                }
            }
        }
        static void channelCRUDTest (ChannelService channelService){
            // 생성
            Channel channel = channelService.create("공지", "공지합니다", "이곳은 공지 채널입니다.");
            System.out.println("==== 채널 등록 ====");
            System.out.println("채널 ID: " + channel.getId());

            System.out.println();
            // 조회
            Channel foundChannel = channelService.find(channel.getId());
            System.out.println("==== 채널 정보(단건) ====");
            System.out.println("채널 ID: " + foundChannel.getId());
            System.out.println("채널 프레임: " + foundChannel.getFrame());
            System.out.println("채널 이름: " + foundChannel.getChannelName());
            System.out.println("메세지 내용: " + foundChannel.getDetail());

            System.out.println();
            //전체 조회
            List<Channel> foundChannels = channelService.findAll();
            System.out.println("==== 채널 정보(다건) ====");
            System.out.println("채널 수: " + foundChannels.size());

            System.out.println();

            // 수정
            Channel updatedChannel = channelService.update(channel.getId(), "게시판", "공지사항", "서류 제출 안내");
            System.out.println("==== 채널 정보 수정 ====");
            System.out.println("채널 ID: " + updatedChannel.getId());
            System.out.println("채널 프레임: " + updatedChannel.getFrame());
            System.out.println("채널 이름: " + updatedChannel.getChannelName());
            System.out.println("메세지 내용: " + updatedChannel.getDetail());

            System.out.println();

            // 삭제
            channelService.delete(channel.getId());
            List<Channel> foundChannelsAfterDelete = channelService.findAll();
            System.out.println("==== 채널 삭제 ====");
            System.out.println("채널 수: " + foundChannelsAfterDelete.size());
            System.out.println();
        }
        static void messageCRUDTest (MessageService messageService){
            // 생성
            UUID channelId = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();

            Message message = messageService.create("안녕하세요.", channelId, authorId);
            System.out.println("==== 메세지 등록 ====");
            System.out.println("메세지 ID: " + message.getId());

            System.out.println();

            // 조회
            Message foundMessage = messageService.find(message.getId());
            System.out.println("==== 메세지 조회(단건) ====");
            System.out.println("메시지 ID: " + foundMessage.getId());
            System.out.println("메시지 내용: " + foundMessage.getchat());


            System.out.println();

            // 전체 조회
            List<Message> foundMessages = messageService.findAll();
            System.out.println("==== 메세지 조회(다건) ====");
            System.out.println("메시지 수: " + foundMessages.size());

            System.out.println();

            // 수정
            Message updatedMessage = messageService.update(message.getId(), "반갑습니다.");
            System.out.println("==== 메세지 수정====");
            System.out.println("메시지 내용: " + updatedMessage.getchat());

            System.out.println();
            // 삭제
            messageService.delete(message.getId());
            List<Message> foundMessagesAfterDelete = messageService.findAll();
            System.out.println("==== 메세지 삭제 ====");
            System.out.println("메시지 수: " + foundMessagesAfterDelete.size());

        }
        public static void main (String[]args){
            // 서비스 초기화
            UserService userService = new JCFUserService();
            ChannelService ChannelService = new JCFChannelService();
            MessageService messageService = new JCFMessageService();

            // 테스트
            userCRUDTest(userService);
            channelCRUDTest(ChannelService);
            messageCRUDTest(messageService);
        }
}


