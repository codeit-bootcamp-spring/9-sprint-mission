package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(length = 50, nullable = false)
    private String fileName;
    @Column
    private Long size;
    @Column(length = 50, nullable = false)
    private String contentType;

    @OneToOne(mappedBy = "profile")
    @ToString.Exclude
    private User user;

    public BinaryContent(String fileName, Long size, String contentType) {
        super();
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
    }
}
