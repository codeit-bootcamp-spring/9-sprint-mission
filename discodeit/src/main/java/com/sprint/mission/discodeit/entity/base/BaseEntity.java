package com.sprint.mission.discodeit.entity.base;


import jakarta.persistence.Column;

import jakarta.persistence.EntityListeners;

import jakarta.persistence.Id;

import jakarta.persistence.MappedSuperclass;

import lombok.Getter;

import org.springframework.data.annotation.CreatedDate;

import org.springframework.data.annotation.LastModifiedDate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.Instant;

import java.util.UUID;


@Getter

@MappedSuperclass

@EntityListeners(AuditingEntityListener.class)

public abstract class BaseEntity {


  @Id

  @Column(name = "id", updatable = false, nullable = false)

  private UUID id;


  @CreatedDate

  @Column(name = "created_at", nullable = false, updatable = false)

  private Instant createdAt;


  @LastModifiedDate

  @Column(name = "updated_at", nullable = false)

  private Instant updatedAt;


  protected BaseEntity() {

    this.id = UUID.randomUUID();

  }

}