package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.UserConverter;
import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.UserRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    public String addUser(UserRequestDTO userRequestDTO){
        User user= UserConverter.convertUserRequestDtoIntoUser(userRequestDTO);
        userRepository.save(user);
        return "User Saved Successfully";
    }

    public User getUserById(int id){
        Optional<User> userOptional=userRepository.findById(id);
        if(userOptional.isPresent()){
            return userOptional.get();
        }
        else{
            return null;
        }
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public String deleteUserById(int id){
        User user=getUserById(id);
        if(user!=null){
            userRepository.deleteById(id);
            return "User Deleted Successfully";
        }
        else {
            return "User not found";
        }
    }


    public String updateUser(int id, UserRequestDTO userRequestDTO){
        User user=new User();
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPasswordHash(userRequestDTO.getPasswordHash());
        user.setRole(userRequestDTO.getRole());
        user.setPreferredLanguage(userRequestDTO.getPreferredLanguage());
        userRepository.save(user);
        return "User Updated Successfully";
    }
}

