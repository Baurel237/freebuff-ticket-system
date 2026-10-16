package com.example.demo.service;

import com.example.demo.dto.request.CreateTicketRequest;
import com.example.demo.dto.response.TicketResponse;
import com.example.demo.entity.Ticket;
import com.example.demo.enums.StatutTicket;
import com.example.demo.exception.TicketAlreadyClosedException;
import com.example.demo.exception.TicketNotFoundException;
import com.example.demo.exception.TicketTitleAlreadyUsedException;
import com.example.demo.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        if (ticketRepository.existsByTitre(request.titre())) {
            throw new TicketTitleAlreadyUsedException(request.titre());
        }
        Ticket ticket = new Ticket(request.titre(), request.description());
        Ticket saved = ticketRepository.save(ticket);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByStatut(StatutTicket statut) {
        return ticketRepository.findByStatut(statut).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TicketResponse closeTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        if (ticket.getStatut() == StatutTicket.FERME) {
            throw new TicketAlreadyClosedException(id);
        }

        ticket.setStatut(StatutTicket.FERME);
        Ticket updated = ticketRepository.save(ticket);
        return toResponse(updated);
    }

    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitre(),
                ticket.getDescription(),
                ticket.getStatut(),
                ticket.getDateCreation()
        );
    }
}
