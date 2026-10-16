package com.example.demo.exception;

public class TicketAlreadyClosedException extends RuntimeException {
    public TicketAlreadyClosedException(Long id) {
        super("Le ticket d'identifiant " + id + " est déjà fermé et ne peut plus être modifié.");
    }
}
