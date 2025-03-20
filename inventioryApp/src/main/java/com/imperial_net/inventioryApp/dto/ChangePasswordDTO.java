package com.imperial_net.inventioryApp.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChangePasswordDTO {
 private String currentPassword;
 private String newPassword;
 private String repeatPassword;
}
