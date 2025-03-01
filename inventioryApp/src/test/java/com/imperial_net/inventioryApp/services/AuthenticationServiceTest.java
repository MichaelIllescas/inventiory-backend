package com.imperial_net.inventioryApp.services;

import static org.junit.jupiter.api.Assertions.*;



import com.imperial_net.inventioryApp.dto.AuthenticationRequest;
import com.imperial_net.inventioryApp.dto.AuthenticationResponse;
import com.imperial_net.inventioryApp.exceptions.DisabledUserException;
import com.imperial_net.inventioryApp.exceptions.InvalidCredentialsException;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.UserRepository;
import com.imperial_net.inventioryApp.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //Le dice a JUnit 5 que use Mockito para ejecutar la prueba.
class AuthenticationServiceTest {

    @Mock // le dice a Mockito que simule el UserRepository.
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks //crea una instancia de AuthenticationService, inyectando los @Mock
    private AuthenticationService authenticationService;

    private User testUser;

    @BeforeEach // Se ejecuta antes de cada test.
    void setUp() { //guarda datos
        testUser = User.builder()
                .id(1L)
                .email("admin@admin.com")
                .password("admin")
                .enabled(true)
                .build();
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        // Simulación de datos
        AuthenticationRequest request = new AuthenticationRequest("admin@admin.com", "admin");

        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(HashMap.class), eq(testUser))).thenReturn("mocked_jwt_token");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(testUser, request.getPassword()));


        // Ejecutar autenticación
        AuthenticationResponse response = authenticationService.login(request);

        // Verificar el resultado
        assertNotNull(response); //La respuesta no debe ser null.
        assertEquals("mocked_jwt_token", response.getJwt()); //El JWT devuelto debe ser "mocked_jwt_token".

        // Verificar llamadas a mocks
        verify(userRepository).findByEmail("admin@admin.com");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(any(HashMap.class), eq(testUser));
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        AuthenticationRequest request = new AuthenticationRequest("nonexistent@example.com", "password");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(request));

        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(authenticationManager, jwtService);
    }

    @Test
    void login_ShouldThrowException_WhenUserIsDisabled() {
        testUser.setEnabled(false);
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        assertThrows(DisabledUserException.class, () -> authenticationService.login(request));

        verify(userRepository).findByEmail("test@example.com");
        verifyNoInteractions(authenticationManager, jwtService);
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsIncorrect() {
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "wrongpassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doThrow(new BadCredentialsException("Contraseña incorrecta."))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(request));

        verify(userRepository).findByEmail("test@example.com");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService);
    }
}
