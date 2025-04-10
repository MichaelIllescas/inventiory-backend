package com.imperial_net.inventioryApp.users.controller;

import com.imperial_net.inventioryApp.users.dto.ChangePasswordDTO;
import com.imperial_net.inventioryApp.users.dto.UserDTO;
import com.imperial_net.inventioryApp.users.dto.UserRequestDTO;
import com.imperial_net.inventioryApp.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para gestionar las operaciones relacionadas con los usuarios.
 * Permite registrar, listar, actualizar, cambiar el estado y la contraseña de los usuarios.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;  // Servicio para gestionar usuarios

    /**
     * Registra un nuevo usuario.
     *
     * @param userRequest DTO con los datos del usuario a registrar.
     * @return Mensaje de éxito.
     */
    @PostMapping("/userRegister")
    public ResponseEntity<Map<String, String>> userRegister(@RequestBody UserRequestDTO userRequest) {
        userService.save(userRequest);  // Llama al servicio para guardar el usuario
        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario registrado correctamente");  // Respuesta de éxito
        return ResponseEntity.status(HttpStatus.CREATED).body(response);  // Retorna un código 201 de creado
    }

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return Lista de usuarios.
     */
    @GetMapping("/getUsers")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());  // Llama al servicio para obtener los usuarios
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param id           ID del usuario a actualizar.
     * @param userRequest DTO con los nuevos datos del usuario.
     * @return Mensaje de éxito.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userRequest) {
        userService.updateUser(id, userRequest);  // Llama al servicio para actualizar el usuario
        return ResponseEntity.status(HttpStatus.OK).body("Usuario actualizado correctamente");  // Respuesta de éxito
    }

    /**
     * Cambia el estado de un usuario (activo/inactivo).
     *
     * @param id        ID del usuario.
     * @param userDTO   DTO con el nuevo estado del usuario.
     * @return Mensaje de éxito o error.
     */
    @PutMapping("/{id}/state")
    public ResponseEntity<?> changeUserState(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            userService.changeUserState(id, userDTO.getState());  // Llama al servicio para cambiar el estado del usuario
            return ResponseEntity.ok("Estado del usuario actualizado correctamente");  // Respuesta de éxito
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());  // Respuesta de error si algo falla
        }
    }

    /**
     * Cambia la contraseña de un usuario.
     *
     * @param request          Petición HTTP que contiene los datos de la sesión del usuario.
     * @param changePasswordDTO DTO con la nueva contraseña.
     * @return Mensaje de éxito.
     */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(HttpServletRequest request, @RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(request, changePasswordDTO);  // Llama al servicio para cambiar la contraseña
        return ResponseEntity.ok("Contraseña actualizada correctamente");  // Respuesta de éxito
    }

    /**
     * Obtiene los datos del usuario que está actualmente en la sesión.
     *
     * @param request Petición HTTP que contiene los datos de la sesión.
     * @return DTO con los datos del usuario de la sesión.
     */
    @GetMapping("/getUserSession")
    public ResponseEntity<UserDTO> getUserSession(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getUserSession(request));  // Llama al servicio para obtener el usuario de la sesión
    }
}
