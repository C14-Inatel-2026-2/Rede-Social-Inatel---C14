package br.inatel.redesocial.cliente;

import java.io.IOException;
import java.net.Socket;

public class ClienteHandler {

    private int porta;
    private String host;
    private ClienteFactory clienteFactory;

    public  ClienteHandler(int porta , String host,ClienteFactory clienteFactory)
    {
        this.porta=porta;
        this.host=host;
        this.clienteFactory=clienteFactory;
    }

    public void gerenciarChat()
    {
        try (Socket socket = this.clienteFactory.criarSocket(this.host, this.porta);
             ClienteSocket clienteSocket = this.clienteFactory.criarClienteSocket(socket))
        {
            Cliente cliente = this.clienteFactory.criarCliente(clienteSocket);
            Thread threadReceber = this.clienteFactory.criarThread(cliente);
            threadReceber.start();
            cliente.enviarMensagem(clienteFactory.criarScanner());
        } catch (IOException e) {
            System.out.println("Nao foi possivel conectar ao servidor"+e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
