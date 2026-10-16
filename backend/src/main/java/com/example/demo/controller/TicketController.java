package com.example.demo.controller;

import com.example.demo.dto.request.CreateTicketRequest;
import com.example.demo.dto.response.TicketResponse;
import com.example.demo.enums.StatutTicket;
import com.example.demo.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        TicketResponse created = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Liste des tickets, avec filtrage optionnel par statut
     * (ex : GET /api/tickets?statut=OUVERT).
     * Un statut invalide est rejeté en 400 par la couche MVC
     * (MethodArgumentTypeMismatchException gérée globalement).
     */
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets(
            @RequestParam(required = false) StatutTicket statut) {
        List<TicketResponse> tickets = (statut != null)
                ? ticketService.getTicketsByStatut(statut)
                : ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @PatchMapping("/{id}/fermer")
    public ResponseEntity<TicketResponse> closeTicket(@PathVariable Long id) {
        TicketResponse updated = ticketService.closeTicket(id);
        return ResponseEntity.ok(updated);
    }
}
