package com.sprint.mission.discodeit.DTO;


import java.util.UUID;

public class UserDTO {

    public record CreateUserDTO(
            String userName,
            String email,
            String password,
            String profileImage
    ) {
    }

    public record FindUserDTO(
            UUID id,
            boolean online,
            long lastOnline
    ){}


    public record updateUserDTO(
            UUID id,
            String userName,
            String email,
            String password,
            String profileImage
    ) {}

    public record loginDTO(
            String userName,
            String password
    ){}

    public record deleteDTO(
            UUID id){}


}
