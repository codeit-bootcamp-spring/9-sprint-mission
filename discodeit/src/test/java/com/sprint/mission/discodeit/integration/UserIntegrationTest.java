package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BinaryContentStorage binaryContentStorage;

  @Test
  void createUser_success() throws Exception {

    MockMultipartFile jsonPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        """
            {
              "username": "testUser",
              "email": "test@test.com",
              "password": "password123"
            }
            """.getBytes()
    );

    mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testUser"));
  }

  @Test
  void createUser_fail_duplicateEmail() throws Exception {

    createUser_success();

    MockMultipartFile jsonPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        """
            {
              "username": "anotherUser",
              "email": "test@test.com",
              "password": "password123"
            }
            """.getBytes()
    );

    mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  void findAll_success() throws Exception {

    createUser_success();

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void delete_success() throws Exception {

    MvcResult result = createAndReturn();

    String response = result.getResponse().getContentAsString();
    String userId = JsonPath.read(response, "$.id");

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  void delete_fail_notFound() throws Exception {

    UUID randomId = UUID.randomUUID();

    mockMvc.perform(delete("/api/users/{userId}", randomId))
        .andExpect(status().isBadRequest());
  }

  private MvcResult createAndReturn() throws Exception {

    MockMultipartFile jsonPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        """
            {
              "username": "tempUser",
              "email": "temp@test.com",
              "password": "password123"
            }
            """.getBytes()
    );

    return mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andReturn();
  }
}