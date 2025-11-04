package com.intelliRead.Online.Reading.Paltform.converter;

import com.intelliRead.Online.Reading.Paltform.enums.Status;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.requestDTO.UserRequestDTO;

public class UserConverter {
    public static User convertUserRequestDtoIntoUser(UserRequestDTO userRequestDTO){
        User user = new User();
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPasswordHash(userRequestDTO.getPasswordHash());
        user.setRole(userRequestDTO.getRole());
        user.setPreferredLanguage(userRequestDTO.getPreferredLanguage());
        //user.setStatus(Status.ACTIVE);
        return user;
    }
}