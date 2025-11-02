package com.intelliRead.Online.Reading.Paltform.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRequestDTO {
    private String name;
    private String email;
    private String passwordHash;
    private String preferredLanguage;
    private String adminSecretKey; // Secret key for admin registration
}