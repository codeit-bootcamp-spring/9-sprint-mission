package com.sprint.mission.discodeit.entity;

import lombok.Getter;


import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private  final UUID id;
    private  final UUID userId;
    private final Instant createdAt;
    private Instant updatedAt;
    private  Instant lastedAt;


    public UserStatus(User user){
        this.id = UUID.randomUUID();
        this.userId =user.getId() ;
        this.createdAt = Instant.now();
        this.updatedAt = null;
        this.lastedAt=Instant.now();

    }

    public boolean isOnline(){
        Instant now= Instant.now();
        Instant fiveMinutesAge = now.minusSeconds(300);
        boolean isRecent = lastedAt.isAfter(fiveMinutesAge);
        if(!isRecent){return false;}
        return true;



    }
    public void forceOnline(){
        this.lastedAt=Instant.now().minusSeconds(301);
        this.updatedAt=Instant.now();
    }
    public void updateLastedAt(){
        this.lastedAt=Instant.now();
        this.updatedAt=Instant.now();
    }



}
