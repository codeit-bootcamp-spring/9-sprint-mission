package com.sprint.mission.discodeit.app.router;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoutePrintText {
    static void printText(String text) {
        List<String> retouchText = new ArrayList<>();

        switch(text.toLowerCase()) {
            case "user":
                retouchText.add("사용자");
                retouchText.add("User");
                break;

            case "channel":
                retouchText.add("채널");
                retouchText.add("Channel");
                break;

            case "message":
                retouchText.add("메세지");
                retouchText.add("Message");
                break;

            default:
                return;
        }

        System.out.println(retouchText.get(0) + " 관련 서비스입니다.");
        System.out.println("어떤 서비스를 원하시나요?");
        System.out.println("====================");
        System.out.println("1. " + retouchText.get(1) + " Create");
        System.out.println("2. " + retouchText.get(1) + " Update");
        System.out.println("3. " + retouchText.get(1) + " Read");
        System.out.println("4. " + retouchText.get(1) + " Delete");
        if(retouchText.get(0).equals("채널"))
            System.out.println("5. Private Channel Invite");
    }
}
