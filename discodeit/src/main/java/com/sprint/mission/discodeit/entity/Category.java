package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class Category extends BaseEntity {
    private String name;

    public Category(String name) {
        super();
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
        recordUpdate();
    }
}