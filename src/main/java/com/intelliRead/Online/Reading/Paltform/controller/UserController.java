package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.exception.UserAlreadyExistException;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.requestDTO.UserRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/apies")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            // ✅ Prevent ADMIN registration through API
            if (userRequestDTO.getRole() == Role.ADMIN) {
                return ResponseEntity.badRequest().body("Admin registration not allowed through this endpoint");
            }

            User savedUser = userService.addUser(userRequestDTO);
            return ResponseEntity.ok(savedUser);
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating user: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id){
        User user = userService.getUserById(id);
        if(user != null){
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> findAll(){
        List<User> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable int id){
        String result = userService.deleteUserById(id);
        if(result.equals("User Deleted Successfully")){
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/Update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable int id, @RequestBody UserRequestDTO userRequestDTO){
        String result = userService.updateUser(id, userRequestDTO);
        if(result.equals("User Updated Successfully")){
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}