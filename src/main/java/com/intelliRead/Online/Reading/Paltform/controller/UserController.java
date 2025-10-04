package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.requestDTO.UserRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user/apies")
public class UserController {
    UserService userService;
    @Autowired
    public UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping("/save")
    public String saveUser(@RequestBody UserRequestDTO userRequestDTO){
        return userService.addUser(userRequestDTO);
    }

    @GetMapping("/get/{id}")
    public User getUser(@PathVariable int id){
        return userService.getUserById(id);
    }

    @GetMapping("/getAll")
    public List<User> findAll(){
        return userService.getAllUser();
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUserById(@PathVariable int id){
        return userService.deleteUserById(id);
    }

    @PutMapping("/Update/{id}")
    public String updateUser(@PathVariable int id, @RequestBody UserRequestDTO userRequestDTO){
        return userService.updateUser(id,userRequestDTO);
    }


}
