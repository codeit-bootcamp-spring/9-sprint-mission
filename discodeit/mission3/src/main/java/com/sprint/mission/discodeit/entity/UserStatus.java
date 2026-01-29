package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


@Getter
public class UserStatus implements Serializable {
    private  UUID id;
    private  UUID userId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastedAt;
    private String name;

    public UserStatus(User user){
        this.id = UUID.randomUUID();
        this.userId =user.getId() ;
        this.createdAt = Instant.now();
        this.updatedAt = null;
        this.lastedAt=Instant.now();
        this.name= user.getUsername();
    }

    public boolean isOnline(){
        Instant now= Instant.now();
        Instant fiveMinutesAge = now.minusSeconds(300);
        boolean isRecent = lastedAt.isAfter(fiveMinutesAge);
        if(!isRecent){return false;}
        return true;



    }


}
