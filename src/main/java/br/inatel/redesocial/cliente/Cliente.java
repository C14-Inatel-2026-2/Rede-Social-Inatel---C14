package br.inatel.redesocial.cliente;

import java.io.IOException;
import java.util.Scanner;

public class Cliente implements Runnable {

    private  String mensagemRecebida;
    private ClienteSocket clienteSocket;
    private String mensagemEnviada;

    public Cliente(ClienteSocket clienteSocket)
    {
        if (clienteSocket == null)
            throw new IllegalArgumentException("O Clientesocket não pode ser nulo.");
        this.clienteSocket = clienteSocket;
    }

    @Override
    public void run()
    {
        try {
            receberMensagem();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void receberMensagem() throws IOException {
        while((mensagemRecebida = this.clienteSocket.ReceberMensagem() )!=null )
        {
            System.out.println(mensagemRecebida);
        }
    }

    public void enviarMensagem(Scanner sc) throws IOException {
        do {
            System.out.println("Escreva uma mensagem");
            mensagemEnviada = sc.nextLine();
            this.clienteSocket.EnviarMensagem(mensagemEnviada);
        } while (!mensagemEnviada.equals("saida"));
    }


}
