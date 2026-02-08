package com.sprint.mission.discodeit.DTO;


public class BinaryContentDTO {

    public record CreateBinaryContentDTO(
            byte[] content, String contentType, String fileName
    ) { }

}
