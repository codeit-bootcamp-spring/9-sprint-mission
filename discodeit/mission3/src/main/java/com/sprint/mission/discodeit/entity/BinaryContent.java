package com.sprint.mission.discodeit.entity;

import lombok.Getter;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class BinaryContent {
    private final UUID id;
    private final Instant createdAt;
    private final UUID ownerId;
    private final String filePath;
    private final String fileContent;

    public BinaryContent(UUID ownerId,String filePath,String fileContent){
        this.id= UUID.randomUUID();
        this.createdAt = Instant.now();
        this.ownerId=ownerId;
        this.filePath=filePath;
        this.fileContent=fileContent;



    }
}
