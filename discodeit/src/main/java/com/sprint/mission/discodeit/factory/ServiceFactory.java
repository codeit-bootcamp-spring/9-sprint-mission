package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.manager.DiscordManager;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;

public class ServiceFactory {
    // true면 파일 저장, false면 메모리(JCF) 저장 모드입니다.
    private static final boolean USE_FILE_STORAGE = true;

    public static UserService getUserService() {
        return new BasicUserService(USE_FILE_STORAGE ?
                FileUserRepository.getInstance() : JCFUserRepository.getInstance());
    }

    public static ChannelService getChannelService() {
        return new BasicChannelService(USE_FILE_STORAGE ?
                FileChannelRepository.getInstance() : JCFChannelRepository.getInstance());
    }

    public static MessageService getMessageService() {
        return new BasicMessageService(USE_FILE_STORAGE ?
                FileMessageRepository.getInstance() : JCFMessageRepository.getInstance());
    }

    public static CategoryService getCategoryService() {
        return new BasicCategoryService(USE_FILE_STORAGE ?
                FileCategoryRepository.getInstance() : JCFCategoryRepository.getInstance());
    }

    public static DiscordManager getDiscordManager() {
        return DiscordManager.getInstance();
    }
}