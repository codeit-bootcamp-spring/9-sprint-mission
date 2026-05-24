package com.sprint.mission.discodeit.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Open title definition")
            .description("")
            .version("vo"))
        .servers(List.of(new Server().url("http://localhost:8080").description("Generated server url")));

  }
}
