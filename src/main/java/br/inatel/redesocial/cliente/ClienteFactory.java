package br.inatel.redesocial.cliente;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClienteFactory  {

    public Socket criarSocket(String host, int porta) throws IOException {
        return new Socket(host, porta);
    }
    public ClienteSocket criarClienteSocket(Socket socket) throws IOException {
        return new ClienteSocket(socket);
    }
    public Cliente criarCliente(ClienteSocket clienteSocket) {
        return new Cliente(clienteSocket);
    }
    public Thread criarThread(Cliente cliente) {
        return new Thread(cliente);
    }
    public Scanner criarScanner()
    {
        return new Scanner(System.in);
    }
}











