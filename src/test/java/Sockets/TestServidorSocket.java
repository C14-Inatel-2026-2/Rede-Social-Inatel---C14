package Sockets;

import br.inatel.redesocial.servidor.Servidor;
import br.inatel.redesocial.servidor.ServidorFactory;
import br.inatel.redesocial.servidor.ServidorHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestServidorSocket {

    @Mock
    private ServidorFactory servidorFactory;

    @Mock
    private ServerSocket serverSocket;

    @Mock
    private ServidorHandler handler;

    @Mock
    private Socket cliente;

    @Test
    public void portaMenorInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Servidor(-1, servidorFactory);
        });
    }

    @Test
    public void portaMaiorInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Servidor(65536, servidorFactory);
        });
    }

    @Test
    public void portaZeroInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Servidor(0, servidorFactory);
        });
    }

    @Test
    public void fabricaNulaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Servidor(5000, null);
        });
    }

    @Test
    public void portasLimiteValidas() {
        assertDoesNotThrow(() -> new Servidor(1, servidorFactory));
        assertDoesNotThrow(() -> new Servidor(65535, servidorFactory));
    }

    @Test
    public void portaCriadaIgualPortaUsada() throws IOException {
        when(servidorFactory.criarServidor(5000)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(true);
        new Servidor(5000, servidorFactory).servidorAtivo();
        verify(servidorFactory).criarServidor(5000);
    }

    @Test
    public void naoAceitarClienteSeSocketFechado() throws IOException {
        when(servidorFactory.criarServidor(5000)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(true);
        new Servidor(5000, servidorFactory).servidorAtivo();
        verify(serverSocket, never()).accept();
    }

    @Test
    public void iniciarHandlerParaClienteAceito() throws IOException {
        when(servidorFactory.criarServidor(5000)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false, true);
        when(serverSocket.accept()).thenReturn(cliente);
        when(servidorFactory.criarServidorHandler(eq(cliente), any(), eq(servidorFactory))).thenReturn(handler);
        new Servidor(5000, servidorFactory).servidorAtivo();
        verify(handler).start();
    }
    @Test
    public void naoExcecaoSeAcceptFalhar() throws IOException {
        when(servidorFactory.criarServidor(5000)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false);
        when(serverSocket.accept()).thenThrow(new IOException("falha"));
        Servidor servidor = new Servidor(5000, servidorFactory);
        assertDoesNotThrow(servidor::servidorAtivo);
    }
    @Test
    public void naoExcecaoSeFabricaFalharAoCriarServidor() throws IOException {
        when(servidorFactory.criarServidor(5000)).thenThrow(new IOException("porta em uso"));
        Servidor servidor = new Servidor(5000, servidorFactory);
        assertDoesNotThrow(servidor::servidorAtivo);
    }
    @Test
    public void deveFecharServerSocketAoTerminar() throws IOException {
        when(servidorFactory.criarServidor(5000)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(true);
        new Servidor(5000, servidorFactory).servidorAtivo();
        verify(serverSocket).close();
    }
}