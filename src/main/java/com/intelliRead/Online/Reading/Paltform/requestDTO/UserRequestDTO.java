package com.intelliRead.Online.Reading.Paltform.requestDTO;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    private String name;
    private  String email;
    private  String passwordHash;
    private Role role;
    private String preferredLanguage;
}
