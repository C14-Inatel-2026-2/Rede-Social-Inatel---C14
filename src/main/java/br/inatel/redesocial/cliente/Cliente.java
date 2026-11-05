package br.inatel.redesocial.cliente;

import br.inatel.redesocial.cliente.comandos.IdentificarComandoChat;
import br.inatel.redesocial.cliente.dto.Mensagem;

import java.io.IOException;
import java.util.Scanner;

public class Cliente implements Runnable {

    private  String mensagemRecebida;
    private ClienteSocket clienteSocket;
    private String mensagemEnviada;
    private Mensagem mensagem;
    private String usuario;
    private String email;

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
            System.out.println("Escreva uma mensagem: ");
            mensagemEnviada = sc.nextLine();
            mensagem = IdentificarComandoChat.identificarComando(mensagemEnviada,usuario, email,null);
            this.clienteSocket.EnviarMensagem(mensagem);
        } while (!mensagemEnviada.equals("saida"));
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public ClienteSocket getClienteSocket() {
        return clienteSocket;
    }
    
    public String getUsuario() {
        return usuario;
    }
}
