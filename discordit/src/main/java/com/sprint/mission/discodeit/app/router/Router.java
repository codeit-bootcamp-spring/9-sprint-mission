package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class Router {
    private final RouteMessage routeMessage;
    private final RouteUser routeUser;
    private final RouteChannel routeChannel;
    private final Scanner scanner;
    private final AuthService authService;
    private final UserState userState;

    public void route() {
        int subMenu;
        int menu;

        while(true) {
            boolean isLogin = userState.getUserName().isEmpty();
            System.out.println("====================");
            System.out.println("0. 프로그램 종료하기");
            System.out.println(isLogin ? "1. 로그인하기" :
                    "1. 로그아웃하기 (현재 사용자 : " + userState.getUserName() + ")");
            System.out.println("2. 사용자 관련 서비스");
            System.out.println("3. 채널 관련 서비스");
            System.out.println("4. 메시지 관련 서비스");
            System.out.println("====================");

            menu = scanner.nextInt();
            scanner.nextLine();

            if (menu == 0) System.exit(0);

            switch (menu) {
                case 1:
                    if (isLogin) {
                        login();
                    } else {
                        authService.logout();
                    }
                    break;

                case 2:
                    RoutePrintText.printText("user");
                    subMenu = inputChecker();

                    if(subMenu == -1) continue;

                    routeUser.route(subMenu);
                    break;

                case 3:
                    RoutePrintText.printText("channel");
                    subMenu = inputChecker();

                    if(subMenu == -1) continue;

                    routeChannel.route(subMenu);
                    break;

                case 4:
                    RoutePrintText.printText("message");
                    subMenu = inputChecker();

                    if(subMenu == -1) continue;

                    routeMessage.route(subMenu);
                    break;

                default:
                    return;
            }
        }
    }

    private int inputChecker() {
        try{
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("잘못된 입력값입니다.");
        }
        return -1;
    }

    private void login() {
        System.out.println("로그인 서비스입니다.");
        System.out.println("로그인할 사용자명을 입력해주세요");
        String name = scanner.nextLine().trim();
        System.out.println("해당 사용자의 비밀번호를 입력해주세요");
        String password = scanner.nextLine().trim();

        try {
            authService.login(new LoginDto(name, password));
        } catch (Exception e) {
            System.err.println("[ERROR] " + e);
        }
    }
}
