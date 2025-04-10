/**
 * Servicio que gestiona las operaciones de negocio relacionadas con clientes.
 * Incluye funcionalidades para registrar, listar, actualizar, y cambiar el estado de los clientes,
 * así como validaciones relacionadas con el plan de suscripción.
 */
package com.imperial_net.inventioryApp.clients.service;

import com.imperial_net.inventioryApp.clients.dto.ClientRequestDTO;
import com.imperial_net.inventioryApp.clients.dto.ClientResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ClientException;
import com.imperial_net.inventioryApp.clients.models.Client;
import com.imperial_net.inventioryApp.suscriptions.model.Subscription;
import com.imperial_net.inventioryApp.users.model.User;
import com.imperial_net.inventioryApp.clients.repository.ClientRepository;
import com.imperial_net.inventioryApp.auth.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final CookieService cookieService;

    /**
     * Registra un nuevo cliente asociado al usuario autenticado.
     *
     * @param clientDto datos del cliente
     * @param request   petición HTTP para obtener el usuario
     * @return cliente registrado en formato DTO
     */
    public ClientResponseDTO registerClient(ClientRequestDTO clientDto, HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ClientException("Usuario no autenticado. No se puede registrar el cliente."));

        if (clientRepository.findByDocumentNumberAndCreatedBy_Id(clientDto.getDocumentNumber(), user.getId()).isPresent()) {
            throw new ClientException("El número de documento '" + clientDto.getDocumentNumber() + "' ya está registrado.");
        }

        if (clientRepository.findByEmailAndCreatedBy_Id(clientDto.getEmail(), user.getId()).isPresent()){
            throw new ClientException("El email '" + clientDto.getEmail() + "' ya está registrado en otro cliente.");
        }

        Client client = convertToEntity(clientDto);
        client.setCreatedBy(user);
        client.setActive(true);

        if (this.validateNumberOfRecords(user)) {
            Client savedClient = clientRepository.save(client);
            return convertToDto(savedClient);
        } else {
            throw new ClientException("Ha alcanzado el límite de registros para el plan FREE. Si desea acceder a registros ilimitados, debe suscribirse al plan PRO");
        }
    }

    /**
     * Obtiene todos los clientes asociados al usuario autenticado.
     *
     * @param request petición HTTP que contiene la cookie
     * @return lista de clientes en formato DTO
     */
    public List<ClientResponseDTO> getAllClientsForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ClientException("Usuario no autenticado"));

        return clientRepository.findAllByCreatedBy_Id(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Actualiza la información de un cliente existente.
     *
     * @param id            ID del cliente a actualizar
     * @param clientRequest nuevos datos del cliente
     * @param request       petición HTTP que contiene la sesión
     */
    public void updateClient(Long id, ClientRequestDTO clientRequest, HttpServletRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientException("Cliente no encontrado en la base de datos"));

        validateClientData(id, clientRequest, request);

        updateEntity(client, clientRequest);
        client.setUpdateDate(LocalDate.now());

        clientRepository.save(client);
    }

    /**
     * Valida los datos del cliente antes de registrar o actualizar.
     *
     * @param id            ID del cliente actual (en actualizaciones)
     * @param clientRequest datos del cliente
     * @param request       petición HTTP para obtener el usuario
     */
    private void validateClientData(Long id, ClientRequestDTO clientRequest, HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ClientException("Usuario no autenticado. No se puede registrar el cliente."));

        clientRepository.findByDocumentNumberAndCreatedBy_Id(clientRequest.getDocumentNumber(), user.getId())
                .filter(existingClient -> !existingClient.getId().equals(id))
                .ifPresent(c -> {
                    throw new ClientException("El número de documento '" + clientRequest.getDocumentNumber() + "' ya está registrado.");
                });

        clientRepository.findByEmail(clientRequest.getEmail())
                .filter(existingClient -> !existingClient.getId().equals(id))
                .ifPresent(c -> {
                    throw new ClientException("El email '" + clientRequest.getEmail() + "' ya está registrado.");
                });

        if (clientRequest.getDocumentNumber().length() < 7 || clientRequest.getDocumentNumber().length() > 15) {
            throw new ClientException("El número de documento debe contener entre 7 y 15 dígitos.");
        }
        if (clientRequest.getPhone().length() < 7 || clientRequest.getPhone().length() > 16) {
            throw new ClientException("El teléfono debe contener entre 6 y 15 dígitos .");
        }
    }

    /**
     * Convierte una entidad Client a DTO.
     *
     * @param client entidad de cliente
     * @return objeto ClientResponseDTO
     */
    public ClientResponseDTO convertToDto(Client client) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ClientResponseDTO dto = new ClientResponseDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setLastname(client.getLastname());
        dto.setDocumentNumber(client.getDocumentNumber());
        dto.setLaxId(client.getLaxId());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        dto.setAddress(client.getAddress());
        dto.setCreatedBy(client.getCreatedBy() != null ? client.getCreatedBy().getFirstName() + " " + client.getCreatedBy().getLastName() : null);
        dto.setRegistrationDate(client.getRegistrationDate().format(formatter).toString());
        dto.setUpdateDate((client.getUpdateDate() != null) ? client.getUpdateDate().format(formatter).toString() : null);
        dto.setActive(client.getActive());
        return dto;
    }

    /**
     * Convierte un DTO de cliente a entidad Client.
     *
     * @param dto objeto ClientRequestDTO
     * @return entidad Client
     */
    public Client convertToEntity(ClientRequestDTO dto) {
        Client client = new Client();
        client.setName(dto.getName());
        client.setLastname(dto.getLastname());
        client.setDocumentNumber(dto.getDocumentNumber());
        client.setLaxId(dto.getLaxId());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setAddress(dto.getAddress());
        client.setActive(true);
        return client;
    }

    /**
     * Actualiza los campos de una entidad Client con los datos de un DTO.
     *
     * @param client entidad existente
     * @param dto    datos nuevos
     */
    private void updateEntity(Client client, ClientRequestDTO dto) {
        client.setName(dto.getName());
        client.setLastname(dto.getLastname());
        client.setDocumentNumber(dto.getDocumentNumber());
        client.setLaxId(dto.getLaxId());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setAddress(dto.getAddress());
    }

    /**
     * Cambia el estado de un cliente (activo/inactivo).
     *
     * @param id ID del cliente
     */
    public void toggleClientStatus(Long id) {
        Optional<Client> optionalClient = clientRepository.findById(id);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            client.setActive(!client.getActive());
            clientRepository.save(client);
        }
    }

    /**
     * Valida si el usuario tiene permitido registrar más clientes según su plan de suscripción.
     *
     * @param user usuario autenticado
     * @return true si puede registrar más clientes, false si llegó al límite
     */
    public boolean validateNumberOfRecords(User user) {
        if (user.getSubscription() == Subscription.FREE) {
            return clientRepository.countClientsByUserId(user.getId()) < 10;
        }
        return true;
    }
}