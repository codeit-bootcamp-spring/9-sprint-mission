import entity.User;
import service.UserService;
import service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class JavaApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        User user = new User("전승현", "asdf@gmail.com", "01212301230");
        User user1 = new User("전팝콘", "asd", "213213");
        UserService service = new JCFUserService();
        boolean choice = true;

        while (choice) {
            System.out.println("\n ==== 메뉴 ====");
            System.out.println("1.등록");
            System.out.println("2.조회(단건)");
            System.out.println("3.조회(다건)");
            System.out.println("4.수정");
            System.out.println("5.삭제");
            System.out.println("선택: ");
            int menu = sc.nextInt();
            sc.nextLine();


            switch (menu) {
                case 1:
                    service.addUser(user);
                    service.addUser(user1);

                    break;
                case 2:
                    System.out.println("회원 이름을 입력하시오.: ");
                    String searchName = sc.nextLine();

                    User foundUser = service.getUser(searchName);
                    if (foundUser != null) {
                        System.out.println("회원: " + foundUser);
                    } else {
                        System.out.println("회원이 없습니다.");
                    }

                    break;
                case 3:
                    List<User> users = service.getAllUsers();
                    System.out.println("==== 전체 회원 목록 ====");
                    users.stream()
                            .forEach(System.out::println);
                    break;
                case 4:
                    System.out.println("수정할 대상의 이름을 입력하시오: ");
                    String updatename = sc.nextLine();
                    if (service.getUser(updatename) != null) {
                        System.out.println("새로운 이메일 입력:");
                        String updateEmail = sc.nextLine();
                        System.out.println("새로운 전화번호 입력");
                        String updatePhoneNumber = sc.nextLine();

                        User updatedata = new User(updatename, updateEmail, updatePhoneNumber);
                        boolean Update = service.updateUser(updatedata);
                        if (Update) {
                            System.out.println("성공적으로 수정!");
                        } else {
                            System.out.println("오류 발생");
                        }

                    } else {
                        System.out.println("회원이없음.");
                    }
                    break;
                case 5:
                    System.out.println("삭제할 회원의 이름을 입력하시오: ");
                    String deleteName = sc.nextLine();
                    boolean Delete = service.deleteUser(deleteName);
                    if (Delete) {
                        System.out.println("성공적으로 삭제");
                    } else {
                        System.out.println("회원이 없음.");

                    }
            break;




            }

        }


    }
}

