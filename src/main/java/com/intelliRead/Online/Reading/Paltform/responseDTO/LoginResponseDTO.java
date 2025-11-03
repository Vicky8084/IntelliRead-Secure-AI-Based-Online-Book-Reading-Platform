
package com.intelliRead.Online.Reading.Paltform.responseDTO;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String email;
    private Role role;
    private int userId;
    private String name;
    private String message;
    private boolean success;
}
