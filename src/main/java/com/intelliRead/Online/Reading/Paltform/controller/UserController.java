package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.exception.UserAlreadyExistException;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.UserRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user/apies")
public class UserController {

    UserService userService;
    UserRepository userRepository;
    @Autowired
    public UserController(UserService userService,
                          UserRepository userRepository){
        this.userService = userService;
        this.userRepository=userRepository;
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
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
            }

            String userEmail = (String) session.getAttribute("userEmail");
            if (userEmail == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user in session");
            }

            Optional<User> userOptional = userRepository.findUserByEmail(userEmail);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // Return safe user data without password
                Map<String, Object> safeUser = new HashMap<>();
                safeUser.put("id", user.getId());
                safeUser.put("name", user.getName());
                safeUser.put("email", user.getEmail());
                safeUser.put("role", user.getRole());
                safeUser.put("status", user.getStatus());
                safeUser.put("preferredLanguage", user.getPreferredLanguage());
                return ResponseEntity.ok(safeUser);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user");
        }
    }
}