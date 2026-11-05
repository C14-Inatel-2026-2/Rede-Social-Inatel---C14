package br.inatel.redesocial.exceptions;

public class FalhaNoLoginException extends RuntimeException {
    public FalhaNoLoginException(String message) {
        super(message);
    }
}
