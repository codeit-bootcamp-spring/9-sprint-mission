package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {

        private String channelName;         // 채널 이름
        private String channelDescription;  // 채널 설명
        private boolean isPrivate;          // 비공개 여부(true/false)

        public Channel(String channelName, String channelDescription, boolean isPrivate) {
            super(); // id/createdAt/updatedAt 자동 초기화
            this.channelName = channelName;
            this.channelDescription = channelDescription;
            this.isPrivate = isPrivate;
        }
        // 조회용 getter
        public String getChannelName() {
            return channelName;
        }
        public String getChannelDescription() {
            return channelDescription;
        }
        public boolean isPrivate() {
            return isPrivate;
        }

    /**
     * - 일부만 변경할 수도 있으니 null 체크
     * - isPrivate는 boolean이라 null이 없으니 그대로 대입
     */
        public void update(String channelName, String channelDescription, boolean isPrivate) {
            if (channelName != null) this.channelName = channelName;
            if (channelDescription != null) this.channelDescription = channelDescription;
            this.isPrivate = isPrivate;

            updateTimestamp();
        }
}






