package com.intelliRead.Online.Reading.Paltform.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadRequestDto {
    private int bookId;
    private int userId;
    private String downloadType = "FULL"; // FULL, SAMPLE
}