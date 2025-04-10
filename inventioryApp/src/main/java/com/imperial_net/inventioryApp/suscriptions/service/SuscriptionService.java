package com.imperial_net.inventioryApp.suscriptions.service;

import com.imperial_net.inventioryApp.users.dto.UserRegisterFromLandigPageDTO;
import com.imperial_net.inventioryApp.users.model.Role;
import com.imperial_net.inventioryApp.suscriptions.model.Subscription;
import com.imperial_net.inventioryApp.users.model.User;
import com.imperial_net.inventioryApp.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio que gestiona las suscripciones de los usuarios.
 * Permite registrar usuarios en el sistema con suscripción FREE o PRO.
 */
@Service
@RequiredArgsConstructor
public class SuscriptionService {

    private final UserRepository userRepository;  // Repositorio para gestionar los usuarios
    private final PasswordEncoder passwordEncoder;  // Codificador de contraseñas

    /**
     * Convierte los datos de registro en un objeto de tipo User.
     *
     * @param userRegisterFreeTrialDTO DTO con los datos del usuario desde el formulario de registro.
     * @return El objeto User creado con los datos proporcionados.
     */
    public User converRequestToUser(UserRegisterFromLandigPageDTO userRegisterFreeTrialDTO){
        User user = new User();
        user.setFirstName(userRegisterFreeTrialDTO.getName());  // Nombre del usuario
        user.setLastName(userRegisterFreeTrialDTO.getLastName());  // Apellido del usuario
        user.setEnabled(true);  // El usuario está habilitado
        user.setRole(Role.USER);  // Se asigna el rol de usuario por defecto
        user.setAddress(userRegisterFreeTrialDTO.getAddress());  // Dirección del usuario
        user.setPhone(userRegisterFreeTrialDTO.getPhone());  // Teléfono del usuario
        user.setEmail(userRegisterFreeTrialDTO.getEmail());  // Correo electrónico del usuario
        user.setDocumentNumber(userRegisterFreeTrialDTO.getDocumentNumber());  // Número de documento del usuario

        // Comprobación y encriptación de la contraseña
        if (userRegisterFreeTrialDTO.getPassword().equals(userRegisterFreeTrialDTO.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(userRegisterFreeTrialDTO.getPassword()));  // Encriptación de la contraseña
        } else {
            throw new RuntimeException("Las claves no coinciden");  // Si las contraseñas no coinciden, se lanza un error
        }
        return user;
    }

    /**
     * Registra un usuario con la suscripción FREE.
     *
     * @param userRegisterFreeTrialDTO DTO con los datos del usuario para registrar.
     */
    public void registerFreeTrialUser(UserRegisterFromLandigPageDTO userRegisterFreeTrialDTO){
        User user = this.converRequestToUser(userRegisterFreeTrialDTO);  // Convierte los datos en un objeto User
        user.setSubscription(Subscription.FREE);  // Asigna la suscripción FREE al usuario
        userRepository.save(user);  // Guarda el usuario en la base de datos
    }

    /**
     * Registra un usuario con la suscripción PRO.
     *
     * @param userRegisterFreeTrialDTO DTO con los datos del usuario para registrar.
     */
    public void registerProUser(UserRegisterFromLandigPageDTO userRegisterFreeTrialDTO){
        User user = this.converRequestToUser(userRegisterFreeTrialDTO);  // Convierte los datos en un objeto User
        user.setSubscription(Subscription.PRO);  // Asigna la suscripción PRO al usuario
        userRepository.save(user);  // Guarda el usuario en la base de datos
    }

}
