package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.UserDTO;
import com.imperial_net.inventioryApp.models.Role;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void insertAdminUser(){
        Optional<User> existingUser = userRepository.findByEmail("admin@admin.com");

        if (existingUser.isEmpty()) { // Solo si no existe, lo creamos
            User admin = User.builder()
                    .firstName("Jonathan y Jose")
                    .lastName("Imperial-net")
                    .documentNumber("37757084")
                    .phone("2923530179")
                    .address("Trenque Lauquen")
                    .email("admin@admin.com") // Usar el mismo email que estás buscando
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
        }
    }

    // Conversión de entidad a DTO
    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getFirstName())
                .lastname(user.getLastName())
                .documentNumber(user.getDocumentNumber())
                .phone(user.getPhone())
                .addres(user.getAddress())
                .email(user.getEmail())
                .state(user.isEnabled()?"ACTIVO":"INACTIVO")
                .role(user.getRole())
                .build();
    }

}
