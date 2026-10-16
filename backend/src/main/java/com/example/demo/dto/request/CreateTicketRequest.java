package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 100, message = "Le titre ne peut pas dépasser 100 caractères")
    String titre,

    String description
) {
}
