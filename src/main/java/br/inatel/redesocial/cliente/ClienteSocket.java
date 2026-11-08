package br.inatel.redesocial.cliente;

import br.inatel.redesocial.cliente.dto.Mensagem;
import java.net.Socket;
import java.io.*;
import com.google.gson.Gson;

public class ClienteSocket implements AutoCloseable {
    private Socket socket;
    private BufferedReader leitor;
    private PrintWriter escritor ;
    private final Gson GSON  = new Gson();

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

    public Mensagem ReceberMensagem() throws IOException {
        return GSON.fromJson(leitor.readLine(), br.inatel.redesocial.cliente.dto.Mensagem.class);
    }

    public void EnviarMensagem(Mensagem msg) throws IOException {
        escritor.println(GSON.toJson(msg));
    }

    @Override
    public void close() throws Exception {
        this.socket.close();
    }
}
