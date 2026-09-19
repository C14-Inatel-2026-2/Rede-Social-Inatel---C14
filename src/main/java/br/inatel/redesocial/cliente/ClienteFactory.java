package br.inatel.redesocial.cliente;

import java.io.IOException;
import java.net.Socket;

public class ClienteFactory  {

    public Socket criarSocket(String host, int porta) throws IOException {
        return new Socket(host, porta);
    }
    public ClienteSocket criarClienteSocket(Socket socket) throws IOException {
        return new ClienteSocket(socket);
    }
}











