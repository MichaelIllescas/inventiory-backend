package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.dto.ChangePasswordDTO;
import com.imperial_net.inventioryApp.dto.UserDTO;
import com.imperial_net.inventioryApp.dto.UserRequestDTO;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/userRegister")
    public ResponseEntity<Map<String, String>> userRegister( @RequestBody UserRequestDTO userRequest) {
        userService.save(userRequest);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario registrado correctamente");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/getUsers")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?>updateUser(
            @PathVariable Long id, @RequestBody UserDTO userRequest
    ){
        userService.updateUser(id,userRequest);
        return ResponseEntity.status(HttpStatus.OK).body("usuario actualizado correctamente");
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<?> changeUserState(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            userService.changeUserState(id, userDTO.getState());
            return ResponseEntity.ok("Estado del usuario actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(HttpServletRequest request, @RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(request, changePasswordDTO);
        return ResponseEntity.ok("contraseña actualizada correctamente");
    }

    @GetMapping("/getUserSession")
    public  ResponseEntity<UserDTO> getUserSession (HttpServletRequest request){
        return ResponseEntity.ok(userService.getUserSession(request));
    }
}
