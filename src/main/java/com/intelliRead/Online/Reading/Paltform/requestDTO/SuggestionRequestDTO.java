package com.intelliRead.Online.Reading.Paltform.requestDTO;

import com.intelliRead.Online.Reading.Paltform.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionRequestDTO {

    private String suggestedTitle;
    private String author; //Optional
    private Status status;
}
