package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;

    // 뭘 저장해야할까
    private final BinaryContentOwnerType ownerType;
    private final UUID ownerId;
    public byte[] data;

    public BinaryContent(BinaryContentOwnerType ownerType, UUID ownerId, byte[] data){
        this.id = UUID.randomUUID();
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.data = data;
    }


}
