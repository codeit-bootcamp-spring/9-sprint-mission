package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/binary")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public BinaryContent findById(@PathVariable("binaryContentId") UUID binaryContentId) {
        return binaryContentService.findById(binaryContentId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContent> findAllByIdIn(@RequestParam("ids") List<UUID> ids) {
        return binaryContentService.findAllByIdIn(ids);
    }
}
