package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.ClientRequestDTO;
import com.imperial_net.inventioryApp.dto.ClientResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ClientException;
import com.imperial_net.inventioryApp.models.Client;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.ClientRepository;
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

        Client savedClient = clientRepository.save(client);
        return convertToDto(savedClient);
    }

    public List<ClientResponseDTO> getAllClientsForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ClientException("Usuario no autenticado"));

        return clientRepository.findAllByCreatedBy_Id(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    public void updateClient(Long id, ClientRequestDTO clientRequest, HttpServletRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientException("Cliente no encontrado en la base de datos"));

        validateClientData(id, clientRequest, request);

        updateEntity(client, clientRequest);
        client.setUpdateDate(LocalDate.now());

        clientRepository.save(client);
    }

    private void validateClientData(Long id, ClientRequestDTO clientRequest, HttpServletRequest request) {
        // Validar si el número de documento ya está registrado
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ClientException("Usuario no autenticado. No se puede registrar el cliente."));

        clientRepository.findByDocumentNumberAndCreatedBy_Id(clientRequest.getDocumentNumber(), user.getId())
                .filter(existingClient -> !existingClient.getId().equals(id))
                .ifPresent(c -> {
                    throw new ClientException("El número de documento '" + clientRequest.getDocumentNumber() + "' ya está registrado.");
                });

        // Validar si el email ya está registrado
        clientRepository.findByEmail(clientRequest.getEmail())
                .filter(existingClient -> !existingClient.getId().equals(id))
                .ifPresent(c -> {
                    throw new ClientException("El email '" + clientRequest.getEmail() + "' ya está registrado.");
                });
        if(clientRequest.getDocumentNumber().length()<7 || clientRequest.getDocumentNumber().length()>15){
            throw new ClientException("El número de documento debe contener entre 7 y 15 dígitos.");
        }
        if(clientRequest.getPhone().length()<7 || clientRequest.getPhone().length()>16){
            throw new ClientException("El teléfono debe contener entre 6 y 15 dígitos .");
        }
    }




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

    private void updateEntity(Client client, ClientRequestDTO dto) {
        client.setName(dto.getName());
        client.setLastname(dto.getLastname());
        client.setDocumentNumber(dto.getDocumentNumber());
        client.setLaxId(dto.getLaxId());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setAddress(dto.getAddress());
    }

    public void toggleClientStatus(Long id){
        Optional<Client> optionalClient = clientRepository.findById(id);
        if(optionalClient.isPresent()){
            Client client = optionalClient.get();
            client.setActive(!client.getActive());
            clientRepository.save(client);

        }

    }
}
