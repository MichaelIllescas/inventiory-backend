package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.ChangePasswordDTO;
import com.imperial_net.inventioryApp.dto.UserDTO;
import com.imperial_net.inventioryApp.dto.UserRequestDTO;
import com.imperial_net.inventioryApp.exceptions.UserRegisterException;
import com.imperial_net.inventioryApp.models.ResetToken;
import com.imperial_net.inventioryApp.models.Role;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.ResetTokenRepository;
import com.imperial_net.inventioryApp.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CookieService cookieService;
    private final ResetTokenRepository resetTokenRepository;
    private final EmailService emailService;

    public void insertAdminUser() {
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

    public UserDTO toDTO(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .documentNumber(user.getDocumentNumber())
                .phone(user.getPhone())
                .address(user.getAddress())
                .email(user.getEmail())
                .state(user.isEnabled() ? "ACTIVO" : "INACTIVO")
                .role(user.getRole())
                .registrationDate(user.getRegistrationDate().format(formatter).toString())
                .build();
    }

    public User toEntity(UserRequestDTO dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .documentNumber(dto.getDocumentNumber())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole())
                .enabled(true)
                .build();
    }

    public void save(UserRequestDTO userRequest) {

        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new UserRegisterException("El ususario con email " + userRequest.getEmail() + " ya está registrado.");
        }
        if (userRepository.findByDocumentNumber(userRequest.getDocumentNumber()).isPresent()) {
            throw new UserRegisterException("El número de documento " + userRequest.getDocumentNumber() + " ya está registrado en el sistema.");
        }
        User user = toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encriptar contraseña
        // Guardar usuario en la base de datos
        userRepository.save(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> usersList = userRepository.findAll();
        return usersList.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void updateUser(Long id, UserDTO userRequest) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new UserRegisterException("Usuario no encontrado en la base de datos");
        }

        User user = optionalUser.get();

        // Validar que el email no esté registrado en otro usuario
        if (userRepository.findByEmail(userRequest.getEmail())
                .filter(existingUser -> !existingUser.getId().equals(id)) // Se compara con el `id` del usuario actual
                .isPresent()) {
            throw new UserRegisterException("El email " + userRequest.getEmail() + " ya está registrado en otro usuario.");
        }

        // Validar que el DNI no esté registrado en otro usuario
        if (userRepository.findByDocumentNumber(userRequest.getDocumentNumber())
                .filter(existingUser -> !existingUser.getId().equals(id))
                .isPresent()) {
            throw new UserRegisterException("El DNI " + userRequest.getDocumentNumber() + " ya está registrado en otro usuario.");
        }

        // Actualizar los datos del usuario
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setDocumentNumber(userRequest.getDocumentNumber());
        user.setPhone(userRequest.getPhone());
        user.setAddress(userRequest.getAddress());
        user.setEmail(userRequest.getEmail());
        user.setRole(userRequest.getRole());
        user.setRegistrationDate(user.getRegistrationDate());
        user.setEnabled(userRequest.getState().equals("ACTIVO")?true:false);

        userRepository.save(user);
    }

    public void changeUserState(Long id, String newState) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new UserRegisterException("Usuario no encontrado.");
        }

        User user = optionalUser.get();
        if (!newState.equals("ACTIVO") && !newState.equals("INACTIVO")) {
            throw new UserRegisterException("Estado inválido. Debe ser 'ACTIVO' o 'INACTIVO'.");
        }

        if (newState.equals("ACTIVO")) {
            user.setEnabled(true);
        }
        if (newState.equals("INACTIVO")) {
            user.setEnabled(false);
        }
        userRepository.save(user);
    }

    public void changePassword(HttpServletRequest request, ChangePasswordDTO changePasswordDTO){
        //OBTENER EL USUARIO DESDE LA COOKIE
        Optional<User> optionalUser= cookieService.getUserFromCookie (request);

        if(optionalUser.isEmpty()){
            throw new RuntimeException("Usuario no autenticado");
        }
        User user = optionalUser.get();
        // Verificar si la contraseña actual es correcta
        if(!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())){
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        // validar que la nueva contraseña y la repeticion coincidan

        if(!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getRepeatPassword())){
            throw new RuntimeException("Las nuevas contraseñas no coinciden");
        }
        // validar que la nueva contraseña sea diferente de la actual

        if(passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPassword())){
            throw new RuntimeException("La nueva contraseña no puede ser igual a la actual");
        }
        // encriptar y actualizar la nueva contraseña

        String hashedNewPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        user.setPassword(hashedNewPassword);

        // guardar el usuario con la nueva contraseña
        userRepository.save(user);
    }

    public UserDTO getUserSession(HttpServletRequest request) {
        //OBTENER EL USUARIO DESDE LA COOKIE
        Optional<User> optionalUser= cookieService.getUserFromCookie (request);

        if(optionalUser.isEmpty()){
            throw new RuntimeException("Usuario no autenticado");
        }
        User user = optionalUser.get();

        return  this.toDTO(user);

    }



    public void sendResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken(token, user, LocalDateTime.now().plusMinutes(30));
        resetTokenRepository.save(resetToken);

        // 👉 URL directamente en el método
        String link = "http://localhost:3000/reset-password?token=" + token;

        emailService.send(
                email,
                "Recuperación de contraseña",
                "Haz clic en el siguiente enlace para restablecer tu contraseña:\n" + link + "\n\nEste enlace expirará en 30 minutos."
        );
    }


    public void resetPassword(String token, String newPassword) {
        // Buscar el token en la base de datos
        ResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // Verificar si el token expiró
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace de recuperación ha expirado.");
        }

        // Obtener el usuario asociado al token
        User user = resetToken.getUser();

        // Codificar la nueva contraseña
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);

        // Guardar el usuario con la nueva contraseña
        userRepository.save(user);

        // Eliminar el token para que no se pueda volver a usar
        resetTokenRepository.delete(resetToken);
    }

}
