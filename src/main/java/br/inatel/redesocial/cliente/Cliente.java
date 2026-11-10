package br.inatel.redesocial.cliente;

import br.inatel.redesocial.cliente.comandos.IdentificarComandoChat;
import br.inatel.redesocial.cliente.dto.MensagemCliente;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.LinkedHashSet;

public class Cliente implements Runnable {

    private MensagemCliente mensagemClienteRecebida;
    private ClienteSocket clienteSocket;
    private String mensagemEnviada;
    private MensagemCliente mensagemCliente;
    private String usuario;
    private String email;
    private final Set<String> sala = new LinkedHashSet<>();
    private String salaAtiva ="";

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
        while((mensagemClienteRecebida = this.clienteSocket.ReceberMensagem() )!=null )
        {
            System.out.println(mensagemClienteRecebida.texto);
        }
    }

    public void enviarMensagem(Scanner sc ,Cliente cliente) throws IOException {
        do {
            System.out.println("Escreva uma mensagem: ");
            mensagemEnviada = sc.nextLine();
            mensagemCliente = IdentificarComandoChat.identificarComando(mensagemEnviada,cliente);
            this.clienteSocket.EnviarMensagem(mensagemCliente);
        } while (!mensagemEnviada.equals("saida"));
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setSalaAtiva(String salaAtiva){this.salaAtiva=salaAtiva;}

    public String getSalaAtiva(){return this.salaAtiva;}

    public ClienteSocket getClienteSocket() {
        return clienteSocket;
    }
    
    public String getUsuario() {
        return usuario;
    }

    public String getEmail(){return email;}

    public Set<String> getSala() {return sala;}

    public  void addSala(String sala)
    {
        this.sala.add(sala);
    }

    public  void removeSala(String sala)
    {
        this.sala.remove(sala);
    }
}
