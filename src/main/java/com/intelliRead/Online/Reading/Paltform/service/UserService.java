package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.UserConverter;
import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.enums.Status;
import com.intelliRead.Online.Reading.Paltform.exception.UserAlreadyExistException;
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
    EmailService emailService;
    @Autowired
    public UserService(UserRepository userRepository,
                       EmailService emailService){
        this.userRepository=userRepository;
        this.emailService=emailService;
    }

    public String addUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistException("User already exists!");
        }

        User user = UserConverter.convertUserRequestDtoIntoUser(dto);

        // If ADMIN, make them INACTIVE and send approval email
        if (dto.getRole() == Role.ADMIN) {
            user.setStatus(Status.INACTIVE);
            userRepository.save(user); // Save as inactive

            // Send email to original ADMINs
            emailService.sendAdminApprovalRequest(user);
            return "✅ Admin registration pending approval!";
        }

        // Normal USER → ACTIVE
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        return "✅ User registered successfully!";
    }



    public User getUserById(int id){
        Optional<User> userOptional=userRepository.findById(id);
        return userOptional.orElse(null);
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
        User user=getUserById(id);
        if(user!=null){
            user.setName(userRequestDTO.getName());
            user.setEmail(userRequestDTO.getEmail());
            user.setPasswordHash(userRequestDTO.getPasswordHash());
            user.setRole(userRequestDTO.getRole());
            user.setPreferredLanguage(userRequestDTO.getPreferredLanguage());
            userRepository.save(user);
            return "User Updated Successfully";
        }else{
            return "User not found";
        }
    }
}

