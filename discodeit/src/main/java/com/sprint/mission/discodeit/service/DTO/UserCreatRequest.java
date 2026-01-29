package com.sprint.mission.discodeit.service.DTO;

import com.sprint.mission.discodeit.status.add.BinaryContent;

//record 사용시 final(불변),getter 제공-> DTO에 유용
public record UserCreatRequest(
        String username,
        String email,
        String password,
        BinaryContent profileImage

) {


}
