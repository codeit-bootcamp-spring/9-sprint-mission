package com.sprint.mission.discodeit.entity;

/**
 * 채널 타입을 정의하는 열거형
 * PUBLIC: 모든 사용자가 접근 가능한 공개 채널
 * PRIVATE: 초대된 사용자만 접근 가능한 비공개 채널
 */
public enum ChannelType {
    /** 공개 채널 */
    PUBLIC,

    /** 비공개 채널 */
    PRIVATE
}
