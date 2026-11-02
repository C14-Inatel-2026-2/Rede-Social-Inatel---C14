package br.inatel.redesocial.servidor;

import java.io.*;
import java.net.Socket;
import java.util.Set;

public class ServidorHandler {

    private Socket socket;
    private static Set<PrintWriter> escritores;
    private ServidorFactory servidorFactory;
    private String usuario;
    private ServidorSocket servidorSocket;

    public ServidorHandler(Socket socket, Set<PrintWriter> escritores, ServidorFactory servidorFactory) {
        this.socket = socket;
        this.escritores = escritores;
        this.servidorFactory = servidorFactory;
    }

    @Override
    public void run() {
        executar();
    }

    private void executar() {
        try {
            servidorSocket = servidorFactory.criarServidorSocket(socket, servidorFactory);

            synchronized (escritores) {
                escritores.add(servidorSocket.getEscritor());
            }
            usuario = servidorSocket.leitura();
            servidorSocket.enviar(usuario, "novo usuario : ", escritores);

            servidorSocket.receber(usuario, escritores);

            synchronized (escritores) {
                escritores.remove(servidorSocket.getEscritor());
            }
            servidorSocket.enviar(usuario, "saiu : ", escritores);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}