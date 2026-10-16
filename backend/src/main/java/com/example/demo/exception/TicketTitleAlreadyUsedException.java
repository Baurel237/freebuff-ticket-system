package com.example.demo.exception;

public class TicketTitleAlreadyUsedException extends RuntimeException {
    public TicketTitleAlreadyUsedException(String titre) {
        super("Le titre \"" + titre + "\" est déjà utilisé. Veuillez en choisir un autre.");
    }
}
