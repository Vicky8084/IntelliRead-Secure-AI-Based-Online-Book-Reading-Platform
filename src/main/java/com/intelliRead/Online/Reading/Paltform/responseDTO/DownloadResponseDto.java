package com.intelliRead.Online.Reading.Paltform.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadResponseDto {
    private boolean success;
    private String message;
    private String downloadUrl;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private int downloadCount;
}