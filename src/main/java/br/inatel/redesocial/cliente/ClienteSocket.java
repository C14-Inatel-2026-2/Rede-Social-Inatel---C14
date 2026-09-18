package br.inatel.redesocial.cliente;
import java.net.Socket;
import java.io.*;

public class ClienteSocket {
    private Socket socket;
    private BufferedReader leitor;
    private PrintWriter escritor ;

    public ClienteSocket(Socket socket) throws IOException {
        if (socket == null)
            throw new IllegalArgumentException("O socket não pode ser nulo.");
        if (socket.isClosed())
            throw new IOException("O socket injetado ja esta fechado.");
        if (!socket.isConnected())
            throw new IOException("O socket injetado nao esta conectado.");

        this.socket = socket;
        leitor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        escritor = new PrintWriter(socket.getOutputStream(), true);
    }

    public String ReceberMensagem() throws IOException {
        return leitor.readLine();
    }

    public Void EnviarMensagem(String mensagemEnviada) throws IOException {
        escritor.println(mensagemEnviada);
        return null;
    }
}
