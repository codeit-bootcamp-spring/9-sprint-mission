package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.UUID;

@ToString
@Getter
public class BinaryContent extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private final BinaryContentOwnerType ownerType;
    private final UUID ownerId;
    public byte[] data;

    public BinaryContent(BinaryContentOwnerType ownerType, UUID ownerId, byte[] data){
        super();
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.data = data;

        System.out.println("BinaryContent 생성 - " + this.toString());
    }
}
