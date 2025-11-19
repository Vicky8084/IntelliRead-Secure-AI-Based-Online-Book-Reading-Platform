package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.requestDTO.DownloadRequestDto;
import com.intelliRead.Online.Reading.Paltform.responseDTO.DownloadResponseDto;
import com.intelliRead.Online.Reading.Paltform.service.DownloadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/download")
@CrossOrigin(origins = "*")
public class DownloadController {

    @Autowired
    private DownloadService downloadService;

    @PostMapping("/prepare")
    public DownloadResponseDto prepareDownload(@RequestBody DownloadRequestDto downloadRequest,
                                               HttpServletRequest request) {
        return downloadService.prepareDownload(downloadRequest, request);
    }

    @GetMapping("/file/{bookId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable int bookId,
                                                 HttpServletRequest request) {
        return downloadService.downloadBookFile(bookId, request);
    }

    @GetMapping("/user/{userId}/count")
    public int getUserDownloadCount(@PathVariable int userId) {
        return downloadService.getUserDownloadCount(userId);
    }

    @GetMapping("/book/{bookId}/count")
    public int getBookDownloadCount(@PathVariable int bookId) {
        return downloadService.getBookDownloadCount(bookId);
    }

    @GetMapping("/check")
    public boolean hasUserDownloadedBook(@RequestParam int userId,
                                         @RequestParam int bookId) {
        return downloadService.hasUserDownloadedBook(userId, bookId);
    }
}