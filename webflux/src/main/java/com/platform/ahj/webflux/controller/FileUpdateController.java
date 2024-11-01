package com.platform.ahj.webflux.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class FileUpdateController {
    @PostMapping("/file")
    public String updateFile(@RequestPart("file-data")FilePart filePart) throws InterruptedException {
        filePart.content().subscribe(dataBuffer -> {
            log.info("dataBuffer:{}",dataBuffer);
        });
        Thread.sleep(1000);
        return "update file";
    }
}
