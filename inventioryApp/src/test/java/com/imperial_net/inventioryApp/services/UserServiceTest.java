package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.users.dto.UserDTO;
import com.imperial_net.inventioryApp.users.dto.UserRequestDTO;
import com.imperial_net.inventioryApp.exceptions.UserRegisterException;
import com.imperial_net.inventioryApp.users.model.Role;
import com.imperial_net.inventioryApp.users.model.User;
import com.imperial_net.inventioryApp.users.repository.UserRepository;
import com.imperial_net.inventioryApp.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Configuración antes de cada test
    }
    @Test
    void insertAdminUser_WhenNoAdminExists_ShouldCreateAdmin() {
        // Configurar comportamiento del repositorio para simular que NO hay admin
        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.empty());

        // Llamar al método bajo prueba
        userService.insertAdminUser();

        // Verificar que el método save fue llamado una vez
        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    void insertAdminUser_WhenAdminExists_ShouldNotCreateAdmin() {
        // Simular un usuario ya existente
        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.of(new User()));

        // Llamar al método
        userService.insertAdminUser();

        // Verificar que NO se llamó a save
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void save_WhenUserIsNew_ShouldSaveUser() {
        UserRequestDTO dto = new UserRequestDTO("Jonathan", "Imperial-net", "37757084", "2923530179",
                "Trenque Lauquen", "jonathan@example.com", "password123", Role.USER);

        // Simular que no hay usuarios con el mismo email o documento
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByDocumentNumber(dto.getDocumentNumber())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashedPassword");

        // Ejecutar el método
        userService.save(dto);

        // Verificar que el usuario se guarda en la base de datos
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void save_WhenEmailExists_ShouldThrowException() {
        UserRequestDTO dto = new UserRequestDTO("Jonathan", "Imperial-net", "37757084", "2923530179",
                "Trenque Lauquen", "jonathan@example.com", "password123", Role.USER);

        // Simular que el email ya existe
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));

        // Verificar que lanza excepción
        Exception exception = assertThrows(UserRegisterException.class, () -> userService.save(dto));

        // Verificar el mensaje de error
        assertEquals("El ususario con email jonathan@example.com ya está registrado.", exception.getMessage());

        // Verificar que nunca se intentó guardar
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void toDTO_ShouldConvertUserToUserDTO() {
        // Crear un usuario de prueba
        User user = User.builder()
                .id(1L)
                .firstName("Jonathan")
                .lastName("Imperial-net")
                .documentNumber("37757084")
                .phone("2923530179")
                .address("Trenque Lauquen")
                .email("jonathan@example.com")
                .role(Role.USER)
                .enabled(true)
                .registrationDate(LocalDate.of(2023, 1, 1)) // Fecha de registro ficticia
                .build();

        // Ejecutar el método
        UserDTO dto = userService.toDTO(user);

        // Verificar que los valores del DTO son los esperados
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getFirstName(), dto.getFirstName());
        assertEquals(user.getLastName(), dto.getLastName());
        assertEquals(user.getDocumentNumber(), dto.getDocumentNumber());
        assertEquals(user.getPhone(), dto.getPhone());
        assertEquals(user.getAddress(), dto.getAddress());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals("ACTIVO", dto.getState()); // El usuario está activo
        assertEquals("01/01/2023", dto.getRegistrationDate()); // Formato esperado
    }
    @Test
    void toEntity_ShouldConvertUserRequestDTOToUser() {
        // Crear un DTO de usuario de prueba
        UserRequestDTO dto = new UserRequestDTO("Jonathan", "Imperial-net", "37757084", "2923530179",
                "Trenque Lauquen", "jonathan@example.com", "password123", Role.USER);

        // Ejecutar el método
        User user = userService.toEntity(dto);

        // Verificar que los valores del usuario sean correctos
        assertEquals(dto.getFirstName(), user.getFirstName());
        assertEquals(dto.getLastName(), user.getLastName());
        assertEquals(dto.getDocumentNumber(), user.getDocumentNumber());
        assertEquals(dto.getPhone(), user.getPhone());
        assertEquals(dto.getAddress(), user.getAddress());
        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals(dto.getRole(), user.getRole());
        assertTrue(user.isEnabled()); // El usuario debe estar activo por defecto
    }
    @Test
    void getAllUsers_ShouldReturnUserDTOList() {
        // Crear lista de usuarios simulada
        List<User> users = List.of(
                new User(1L, "Jonathan", "Imperial-net", "37757084", "2923530179", "Trenque Lauquen",
                        "jonathan@example.com", "hashedPassword",  true,Role.USER, LocalDate.of(2023, 1, 1)),
                new User(2L, "Jose", "Imperial-net", "12345678", "2923123456", "Buenos Aires",
                        "jose@example.com", "hashedPassword",  false,Role.ADMIN, LocalDate.of(2022, 6, 15))
        );

        // Simular que `findAll()` devuelve esta lista
        when(userRepository.findAll()).thenReturn(users);

        // Ejecutar el método
        List<UserDTO> result = userService.getAllUsers();

        // Verificar que el tamaño de la lista es el esperado
        assertEquals(2, result.size());

        // Verificar algunos valores del primer usuario
        assertEquals("Jonathan", result.get(0).getFirstName());
        assertEquals("ACTIVO", result.get(0).getState());

        // Verificar algunos valores del segundo usuario
        assertEquals("Jose", result.get(1).getFirstName());
        assertEquals("INACTIVO", result.get(1).getState());
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateUser() {
        // Simular un usuario existente en la base de datos
        User existingUser = new User(1L, "Jonathan", "Imperial-net", "37757084", "2923530179",
                "Trenque Lauquen", "jonathan@example.com", "hashedPassword", true,Role.USER,  LocalDate.of(2023, 1, 1));

        // Simular el request de actualización
        UserDTO updateRequest = new UserDTO(1L, "Jon", "Doe", "99999999", "123456789",
                "New Address", "newemail@example.com", "ACTIVO", Role.ADMIN, "01/01/2023");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByDocumentNumber(updateRequest.getDocumentNumber())).thenReturn(Optional.empty());

        // Ejecutar el método
        userService.updateUser(1L, updateRequest);

        // Verificar que los valores fueron actualizados
        assertEquals("Jon", existingUser.getFirstName());
        assertEquals("Doe", existingUser.getLastName());
        assertEquals("99999999", existingUser.getDocumentNumber());
        assertEquals("123456789", existingUser.getPhone());
        assertEquals("New Address", existingUser.getAddress());
        assertEquals("newemail@example.com", existingUser.getEmail());
        assertEquals(Role.ADMIN, existingUser.getRole());

        // Verificar que se llamó a save() una vez
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDTO updateRequest = new UserDTO(1L, "Jon", "Doe", "99999999", "123456789",
                "New Address", "newemail@example.com", "ACTIVO", Role.ADMIN, "01/01/2023");

        Exception exception = assertThrows(UserRegisterException.class, () -> userService.updateUser(1L, updateRequest));
        assertEquals("Usuario no encontrado en la base de datos", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void changeUserState_WhenUserExists_ShouldChangeState() {
        User user = new User(1L, "Jonathan", "Imperial-net", "37757084", "2923530179",
                "Trenque Lauquen", "jonathan@example.com", "hashedPassword",  true,Role.USER, LocalDate.of(2023, 1, 1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.changeUserState(1L, "INACTIVO");

        assertFalse(user.isEnabled()); // Debe estar inactivo
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changeUserState_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserRegisterException.class, () -> userService.changeUserState(1L, "INACTIVO"));
        assertEquals("Usuario no encontrado.", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }


}