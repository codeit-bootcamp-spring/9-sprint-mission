package com.sprint.mission.discodeit.entity;

import lombok.Getter;


import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private final String filePath;
    private final String contentType;
    private final String fileName;



    public BinaryContent(UUID id,String filePath,String contentType,String fileName){
        this.id= id;
        this.createdAt = Instant.now();
        this.filePath=filePath;
        this.contentType=contentType;
        this.fileName=fileName;
    }
//    public BinaryContent(String filePath,String contentType,String fileName){
//        this.id= UUID.randomUUID();
//        this.createdAt = Instant.now();
//        this.filePath=filePath;
//        this.contentType=contentType;
//        this.fileName=fileName;
//
//    }
}
