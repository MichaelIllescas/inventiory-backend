package com.imperial_net.inventioryApp.users.service;

import com.imperial_net.inventioryApp.users.dto.ChangePasswordDTO;
import com.imperial_net.inventioryApp.auth.service.CookieService;
import com.imperial_net.inventioryApp.email.service.EmailService;
import com.imperial_net.inventioryApp.users.dto.UserDTO;
import com.imperial_net.inventioryApp.users.dto.UserRequestDTO;
import com.imperial_net.inventioryApp.exceptions.UserRegisterException;
import com.imperial_net.inventioryApp.users.model.ResetToken;
import com.imperial_net.inventioryApp.users.model.Role;
import com.imperial_net.inventioryApp.users.model.User;
import com.imperial_net.inventioryApp.users.repository.ResetTokenRepository;
import com.imperial_net.inventioryApp.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona las operaciones relacionadas con los usuarios, incluyendo registro, actualización,
 * cambio de contraseña, gestión de estado, y manejo de tokens para recuperación de contraseña.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CookieService cookieService;
    private final ResetTokenRepository resetTokenRepository;
    private final EmailService emailService;

    /**
     * Inserta un usuario administrador si no existe previamente.
     * Se utiliza para crear un usuario administrador inicial en la base de datos.
     */
    public void insertAdminUser() {
        Optional<User> existingUser = userRepository.findByEmail("admin@admin.com");

        if (existingUser.isEmpty()) {
            User admin = User.builder()
                    .firstName("Jonathan y Jose")
                    .lastName("Imperial-net")
                    .documentNumber("37757084")
                    .phone("2923530179")
                    .address("Trenque Lauquen")
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
        }
    }

    /**
     * Convierte una entidad User a su representación DTO (Data Transfer Object).
     *
     * @param user la entidad User
     * @return el objeto UserDTO correspondiente
     */
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
                .subscription(user.getSubscription())
                .build();
    }

    /**
     * Convierte un UserRequestDTO a una entidad User.
     *
     * @param dto el DTO con los datos del usuario
     * @return entidad User
     */
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
                .subscription(dto.getSubscription())
                .build();
    }

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param userRequest DTO con los datos del nuevo usuario
     * @throws UserRegisterException si el email o el documento ya están registrados
     */
    public void save(UserRequestDTO userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new UserRegisterException("El usuario con email " + userRequest.getEmail() + " ya está registrado.");
        }
        if (userRepository.findByDocumentNumber(userRequest.getDocumentNumber()).isPresent()) {
            throw new UserRegisterException("El número de documento " + userRequest.getDocumentNumber() + " ya está registrado en el sistema.");
        }
        User user = toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Obtiene todos los usuarios del sistema.
     *
     * @return lista de usuarios en formato UserDTO
     */
    public List<UserDTO> getAllUsers() {
        List<User> usersList = userRepository.findAll();
        return usersList.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param id          ID del usuario
     * @param userRequest DTO con los nuevos datos del usuario
     * @throws UserRegisterException si el usuario no se encuentra o el email/DNI ya están registrados
     */
    public void updateUser(Long id, UserDTO userRequest) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserRegisterException("Usuario no encontrado en la base de datos");
        }
        User user = optionalUser.get();

        // Verifica si el email o DNI ya están en uso por otro usuario
        if (userRepository.findByEmail(userRequest.getEmail())
                .filter(existingUser -> !existingUser.getId().equals(id))
                .isPresent()) {
            throw new UserRegisterException("El email " + userRequest.getEmail() + " ya está registrado en otro usuario.");
        }
        if (userRepository.findByDocumentNumber(userRequest.getDocumentNumber())
                .filter(existingUser -> !existingUser.getId().equals(id))
                .isPresent()) {
            throw new UserRegisterException("El DNI " + userRequest.getDocumentNumber() + " ya está registrado en otro usuario.");
        }

        // Actualiza la información del usuario
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setDocumentNumber(userRequest.getDocumentNumber());
        user.setPhone(userRequest.getPhone());
        user.setAddress(userRequest.getAddress());
        user.setEmail(userRequest.getEmail());
        user.setRole(userRequest.getRole());
        user.setRegistrationDate(user.getRegistrationDate());
        user.setEnabled("ACTIVO".equals(userRequest.getState()));

        userRepository.save(user);
    }

    /**
     * Cambia el estado de un usuario (ACTIVO/INACTIVO).
     *
     * @param id       ID del usuario
     * @param newState nuevo estado ("ACTIVO" o "INACTIVO")
     * @throws UserRegisterException si el estado es inválido o el usuario no se encuentra
     */
    public void changeUserState(Long id, String newState) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserRegisterException("Usuario no encontrado.");
        }
        User user = optionalUser.get();
        if (!"ACTIVO".equals(newState) && !"INACTIVO".equals(newState)) {
            throw new UserRegisterException("Estado inválido. Debe ser 'ACTIVO' o 'INACTIVO'.");
        }
        user.setEnabled("ACTIVO".equals(newState));
        userRepository.save(user);
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     *
     * @param request            petición HTTP con la cookie de autenticación
     * @param changePasswordDTO  datos de la contraseña actual y nueva
     * @throws RuntimeException si la contraseña actual es incorrecta, o las nuevas contraseñas no coinciden
     */
    public void changePassword(HttpServletRequest request, ChangePasswordDTO changePasswordDTO) {
        Optional<User> optionalUser = cookieService.getUserFromCookie(request);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Usuario no autenticado");
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getRepeatPassword())) {
            throw new RuntimeException("Las nuevas contraseñas no coinciden");
        }
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("La nueva contraseña no puede ser igual a la actual");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Obtiene los datos del usuario autenticado desde la cookie.
     *
     * @param request petición HTTP con cookie de sesión
     * @return datos del usuario en formato UserDTO
     */
    public UserDTO getUserSession(HttpServletRequest request) {
        Optional<User> optionalUser = cookieService.getUserFromCookie(request);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Usuario no autenticado");
        }
        return this.toDTO(optionalUser.get());
    }

    /**
     * Envía un token de recuperación de contraseña al correo del usuario.
     *
     * @param email email del usuario
     */
    public void sendResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken(token, user, LocalDateTime.now().plusMinutes(30));
        resetTokenRepository.save(resetToken);

        String link = "http://localhost:3000/reset-password?token=" + token;
        emailService.send(
                email,
                "Recuperación de contraseña",
                "Haz clic en el siguiente enlace para restablecer tu contraseña:\n" + link + "\n\nEste enlace expirará en 30 minutos."
        );
    }

    /**
     * Restablece la contraseña de un usuario a partir de un token válido.
     *
     * @param token       token de recuperación
     * @param newPassword nueva contraseña
     */
    public void resetPassword(String token, String newPassword) {
        ResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace de recuperación ha expirado.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetTokenRepository.delete(resetToken);
    }
}
