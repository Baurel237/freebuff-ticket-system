package com.example.demo.dto.response;

import com.example.demo.enums.StatutTicket;

import java.time.LocalDateTime;

public record TicketResponse(
    Long id,
    String titre,
    String description,
    StatutTicket statut,
    LocalDateTime dateCreation
) {
}
