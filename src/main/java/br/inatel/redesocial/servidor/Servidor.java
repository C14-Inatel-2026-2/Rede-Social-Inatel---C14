package br.inatel.redesocial.servidor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class Servidor {

    private final int porta;
    private final ServidorFactory servidorFactory;
    private final Set<PrintWriter> escritores = new HashSet<>();

    public Servidor(int porta, ServidorFactory servidorFactory) {
        if (porta < 1 || porta > 65535) {
            throw new IllegalArgumentException("Porta inválida.");
        }

        if (servidorFactory == null) {
            throw new IllegalArgumentException(
                    "A fábrica do servidor não pode ser nula."
            );
        }

        this.porta = porta;
        this.servidorFactory = servidorFactory;
    }


}