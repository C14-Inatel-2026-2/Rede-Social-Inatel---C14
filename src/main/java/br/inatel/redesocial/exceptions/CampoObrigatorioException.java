package br.inatel.redesocial.exceptions;

public class CampoObrigatorioException extends RuntimeException {
    public CampoObrigatorioException(String message) {
        super(message);
    }
}
