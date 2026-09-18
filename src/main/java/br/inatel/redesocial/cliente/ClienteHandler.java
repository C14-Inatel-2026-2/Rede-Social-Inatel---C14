package br.inatel.redesocial.cliente;

import java.io.IOException;
import java.net.Socket;

public class ClienteHandler {

    private int porta;
    private String host;
    public  ClienteHandler(int porta , String host)
    {
        this.porta=porta;
        this.host=host;
    }

    public void gerenciarChat()
    {
        try (Socket socket = new Socket(this.host, this.porta);
             ClienteSocket clienteSocket = new ClienteSocket(socket))
        {
            Cliente cliente = new Cliente(clienteSocket);
            Thread threadReceber = new Thread(cliente);
            System.out.println("Conectado com Sucesso");
            threadReceber.start();
            cliente.enviarMensagem();

        } catch (IOException e) {
            System.out.println("Nao foi possivel conectar ao servidor"+e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
