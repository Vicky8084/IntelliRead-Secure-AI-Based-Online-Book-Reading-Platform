package com.intelliRead.Online.Reading.Paltform.responseDTO;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponseDTO {
    private int userId;
    private String name;
    private String email;
    private Role role;
    private String message;
    private boolean success;
}