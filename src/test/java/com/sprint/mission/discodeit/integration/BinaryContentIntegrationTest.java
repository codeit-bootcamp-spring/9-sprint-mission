package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BinaryContentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BinaryContentService binaryContentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private MessageService messageService;

    @Test
    @DisplayName("바이너리 컨텐츠 단건 조회 성공")
    void findBinaryContent_Success() throws Exception {
        UserDto user = userService.create(
            new UserCreateRequest("contentuser", "content@example.com", "Password1!"),
            Optional.empty());
        var channel = channelService.create(
            new PublicChannelCreateRequest("테스트채널", "설명"));

        byte[] fileContent = "테스트 파일 내용".getBytes();
        MessageDto message = messageService.create(
            new MessageCreateRequest("첨부파일 메시지", channel.id(), user.id()),
            List.of(new BinaryContentCreateRequest("test.txt", MediaType.TEXT_PLAIN_VALUE,
                fileContent)));

        UUID binaryContentId = message.attachments().get(0).id();

        mockMvc.perform(get("/api/binaryContents/{binaryContentId}", binaryContentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(binaryContentId.toString())))
            .andExpect(jsonPath("$.fileName", is("test.txt")))
            .andExpect(jsonPath("$.contentType", is(MediaType.TEXT_PLAIN_VALUE)))
            .andExpect(jsonPath("$.size", is(fileContent.length)));
    }

    @Test
    @DisplayName("존재하지 않는 바이너리 컨텐츠 조회 실패")
    void findBinaryContent_Failure_NotFound() throws Exception {
        mockMvc.perform(get("/api/binaryContents/{binaryContentId}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("여러 바이너리 컨텐츠 조회 성공")
    void findAllByIds_Success() throws Exception {
        UserDto user = userService.create(
            new UserCreateRequest("contentuser2", "content2@example.com", "Password1!"),
            Optional.empty());
        var channel = channelService.create(
            new PublicChannelCreateRequest("테스트채널2", "설명"));

        MessageDto message = messageService.create(
            new MessageCreateRequest("다중 첨부", channel.id(), user.id()),
            List.of(
                new BinaryContentCreateRequest("test1.txt", MediaType.TEXT_PLAIN_VALUE,
                    "파일1".getBytes()),
                new BinaryContentCreateRequest("test2.txt", MediaType.TEXT_PLAIN_VALUE,
                    "파일2".getBytes())));

        List<UUID> ids = message.attachments().stream().map(BinaryContentDto::id).toList();

        mockMvc.perform(get("/api/binaryContents")
                .param("binaryContentIds", ids.get(0).toString())
                .param("binaryContentIds", ids.get(1).toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].fileName", hasItems("test1.txt", "test2.txt")));
    }

    @Test
    @DisplayName("바이너리 컨텐츠 다운로드 성공")
    void download_Success() throws Exception {
        String fileContent = "다운로드 테스트 파일";
        BinaryContentDto bc = binaryContentService.create(
            new BinaryContentCreateRequest("download.txt", MediaType.TEXT_PLAIN_VALUE,
                fileContent.getBytes()));

        mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", bc.id()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE))
            .andExpect(content().bytes(fileContent.getBytes()));
    }

    @Test
    @DisplayName("존재하지 않는 바이너리 컨텐츠 다운로드 실패")
    void download_Failure_NotFound() throws Exception {
        mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }
}
