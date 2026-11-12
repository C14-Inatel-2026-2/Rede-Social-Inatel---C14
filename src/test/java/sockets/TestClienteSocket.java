package sockets;

import br.inatel.redesocial.cliente.ClienteSocket;
import br.inatel.redesocial.cliente.dto.MensagemCliente;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestClienteSocket {

    private Socket socket;

    @BeforeEach
    public void inicializarSocket() {
        socket = Mockito.mock(Socket.class);
    }

    @Test
    public void testSocketNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ClienteSocket(null);
        });
    }

    @Test
    public void testSocketFechado() {
        when(socket.isClosed()).thenReturn(true);

        assertThrows(IOException.class, () -> {
            new ClienteSocket(socket);
        });
    }

    @Test
    public void testSocketDesconectado() {
        when(socket.isClosed()).thenReturn(false);
        when(socket.isConnected()).thenReturn(false);

        assertThrows(IOException.class, () -> {
            new ClienteSocket(socket);
        });
    }

    @Test
    public void testReceberMensagem() throws IOException {

        configurarSocket();

        when(socket.getInputStream()).thenReturn(
                new ByteArrayInputStream(
                        "{\"texto\":\"Ola\"}\n".getBytes(StandardCharsets.UTF_8)
                )
        );

        when(socket.getOutputStream()).thenReturn(
                new ByteArrayOutputStream()
        );

        ClienteSocket clienteSocket = new ClienteSocket(socket);

        assertEquals("Ola", clienteSocket.ReceberMensagem().texto);
    }

    @Test
    public void enviarMensagem() throws IOException {

        when(socket.isClosed()).thenReturn(false);
        when(socket.isConnected()).thenReturn(true);
        when(socket.getInputStream()).thenReturn(
                new ByteArrayInputStream(new byte[0])
        );

        ByteArrayOutputStream saida = new ByteArrayOutputStream();

        when(socket.getOutputStream()).thenReturn(saida);

        ClienteSocket clienteSocket = new ClienteSocket(socket);

        MensagemCliente mensagemCliente = new MensagemCliente();
        mensagemCliente.type = "chat";
        mensagemCliente.email = "teste@email.com";
        mensagemCliente.sala = "sala1";
        mensagemCliente.usuario = "usuario1";
        mensagemCliente.texto = "Ola";
        mensagemCliente.valorInt = 0;

        clienteSocket.EnviarMensagem(mensagemCliente);

        String esperado = new Gson().toJson(mensagemCliente)
                + System.lineSeparator();

        assertEquals(
                esperado,
                saida.toString(StandardCharsets.UTF_8)
        );
    }

    private void configurarSocket() {
        when(socket.isClosed()).thenReturn(false);
        when(socket.isConnected()).thenReturn(true);
    }
}
