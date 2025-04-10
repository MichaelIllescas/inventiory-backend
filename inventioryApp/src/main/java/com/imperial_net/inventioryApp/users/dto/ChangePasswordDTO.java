package com.imperial_net.inventioryApp.users.dto;

import lombok.*;

/**
 * DTO utilizado para cambiar la contraseña de un usuario.
 * Contiene la contraseña actual, la nueva contraseña y la confirmación de la nueva contraseña.
 */
@AllArgsConstructor  // Genera un constructor con todos los parámetros
@NoArgsConstructor   // Genera un constructor sin parámetros
@Getter              // Genera los métodos getter para cada campo
@Setter              // Genera los métodos setter para cada campo
@Builder             // Permite usar el patrón Builder para construir el objeto
public class ChangePasswordDTO {

 /**
  * Contraseña actual del usuario.
  */
 private String currentPassword;

 /**
  * Nueva contraseña que el usuario desea establecer.
  */
 private String newPassword;

 /**
  * Confirmación de la nueva contraseña.
  * Debe coincidir con la nueva contraseña para validar el cambio.
  */
 private String repeatPassword;
}
