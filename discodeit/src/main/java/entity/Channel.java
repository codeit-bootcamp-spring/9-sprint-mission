package entity;

public class Channel extends BaseEntity {

        private String channelName;
        private String channelDescription;
        private boolean isPrivate;

        public Channel(String channelName, String channelDescription, boolean isPrivate) {
            super();
            this.channelName = channelName;
            this.channelDescription = channelDescription;
            this.isPrivate = isPrivate;
        }
        public String getChannelName() {
            return channelName;
        }
        public String getChannelDescription() {
            return channelDescription;
        }
        public boolean isPrivate() {
            return isPrivate;
        }
        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }
        public void setChannelDescription(String channelDescription) {
            this.channelDescription = channelDescription;
        }
        public void setPrivateChannel(boolean isPrivate) {
            this.isPrivate = isPrivate;
        }
}






