package com.imperial_net.inventioryApp.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExpenseResponseDTO {
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        private LocalDate date;
        private String expenseType;
        private Double amount;
        private String paymentMethod;
        private String description;
        private String createdBy;
    }


