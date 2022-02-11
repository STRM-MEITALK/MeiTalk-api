package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.common.enums.Role;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.model.vod.VodRequest;
import com.meitalk.api.service.VodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vod")
@Slf4j
public class VodController {
    private String url = "src/main/resources/video/";
    private final VodService vodService;

    @GetMapping(value = "/video/{name}/{type}")
    public ResponseEntity<ResourceRegion> getVideo(@RequestHeader HttpHeaders headers, @PathVariable String name, @PathVariable String type) throws IOException {
        log.info("VideoController.getVideo");

        UrlResource video = new UrlResource("file:" + url + name + "." + type);
//        System.out.println(video);
//        System.out.println(video.getFile());
//        System.out.println(video.getFile().getName());
//        System.out.println(video.getFile().getAbsolutePath());
//        System.out.println(video.getFile().getCanonicalPath());
        ResourceRegion resourceRegion;

        final long chunkSize = 1024 * 1024L;
        long contentLength = video.contentLength();
        System.out.println(contentLength);

        Optional<HttpRange> optional = headers.getRange().stream().findFirst();
        HttpRange httpRange;
        if (optional.isPresent()) {
            httpRange = optional.get();
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = Long.min(chunkSize, end - start + 1);
            System.out.println(rangeLength);
            resourceRegion = new ResourceRegion(video, start, rangeLength);
        } else {
            long rangeLength = Long.min(chunkSize, contentLength);
            resourceRegion = new ResourceRegion(video, 0, rangeLength);
        }
        System.out.println(MediaTypeFactory.getMediaType(video));

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resourceRegion);
    }

    @PostMapping("/update/{chatKey}")
    public ResponseWithData updateDetailVod(
            @JwtAuthentication(role = Role.STREAMER) JwtUser user,
            @PathVariable String chatKey,
            @RequestBody VodRequest.UpdateDetail dto
    ) {
        vodService.saveRealDetailVod(chatKey, dto);
        return ResponseWithData.success();
    }

    @PostMapping("/thumbnail/{vodId}")
    public ResponseWithData updateVodThumbnail(
            @JwtAuthentication(role = Role.STREAMER) JwtUser user,
            @PathVariable Long vodId,
            @RequestParam("images") MultipartFile multipartFile
    ) throws IOException {
        vodService.updateVodThumbnail(user.getUserNo(), vodId, multipartFile);
        return ResponseWithData.success();
    }
}
