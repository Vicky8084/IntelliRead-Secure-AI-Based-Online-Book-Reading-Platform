package com.intelliRead.Online.Reading.Paltform.requestDTO;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    private String name;
    private  String email;
    private  String passwordHash;
    private Role role;  //ADMIN/USER
    private String preferredLanguage;
}
