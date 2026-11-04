package Sockets;

import br.inatel.redesocial.autenticacao.Usuario;
import br.inatel.redesocial.servidor.GerenciadorSalas;
import br.inatel.redesocial.servidor.ServidorFactory;
import br.inatel.redesocial.servidor.ServidorHandler;
import br.inatel.redesocial.servidor.ServidorSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestServidorHandler {
    @Mock
    private ServidorFactory servidorFactory;
    @Mock
    private Socket socket;
    @Mock
    private ServidorSocket servidorSocket;
    @Mock
    private PrintWriter escritor;
    private Set<PrintWriter> escritores;
    private ServidorHandler handler;
    @BeforeEach
    void criarHandler() throws Exception {
        escritores = new HashSet<>();
        handler = new ServidorHandler(socket, escritores, servidorFactory);
        when(servidorFactory.criarServidorSocket(socket, servidorFactory)).thenReturn(servidorSocket);
        when(servidorSocket.getEscritor()).thenReturn(escritor);
    }
    @Test
    public void enviaAvisoDeNovoUsuario() throws Exception {
        GerenciadorSalas.addSala(10, "hNovo");
        when(servidorSocket.leitura()).thenReturn("thiago", "hNovo");
        handler.run();
        verify(servidorSocket).enviar("thiago\u0001hNovo", "novo usuario : ", escritores, "hNovo");
    }
    @Test
    public void chamaReceber() throws Exception {
        GerenciadorSalas.addSala(10, "hReceber");
        when(servidorSocket.leitura()).thenReturn("thiago", "hReceber");
        handler.run();
        verify(servidorSocket).receber("thiago\u0001hReceber", escritores, "hReceber");
    }
    @Test
    public void enviaAvisoDeSaida() throws Exception {
        GerenciadorSalas.addSala(10, "hSaida");
        when(servidorSocket.leitura()).thenReturn("thiago", "hSaida");
        handler.run();
        verify(servidorSocket).enviar("thiago\u0001hSaida", "saiu : ", escritores, "hSaida");
    }
    @Test
    public void removeEscritorNoFinal() throws Exception {
        GerenciadorSalas.addSala(10, "hRemove");
        when(servidorSocket.leitura()).thenReturn("thiago", "hRemove");
        handler.run();
        assertFalse(escritores.contains(escritor));
    }
    @Test
    public void salaInexistente() throws Exception {
        when(servidorSocket.leitura()).thenReturn("thiago", "hNaoExiste");
        assertThrows(IllegalArgumentException.class, handler::run);
    }
    @Test
    public void salaInexistenteNaoRecebe() throws Exception {
        when(servidorSocket.leitura()).thenReturn("thiago", "hNaoExiste2");
        assertThrows(IllegalArgumentException.class, handler::run);
        verify(servidorSocket, never()).receber(any(), any(), any());
    }
    @Test
    public void salaLotada() throws Exception {
        GerenciadorSalas.addSala(1, "hLotada");
        GerenciadorSalas.getSala("hLotada").addUsuario(mock(Usuario.class));
        when(servidorSocket.leitura()).thenReturn("thiago", "hLotada");
        assertThrows(IllegalArgumentException.class, handler::run);
    }
    @Test
    public void erroNaLeitura() throws Exception {
        when(servidorSocket.leitura()).thenThrow(new IOException("falha"));
        assertDoesNotThrow(handler::run);
    }
}