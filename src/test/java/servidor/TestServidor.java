package servidor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.inatel.redesocial.servidor.Servidor;
import br.inatel.redesocial.servidor.ServidorFactory;
import br.inatel.redesocial.servidor.ServidorHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ResourceLock(Resources.SYSTEM_ERR)
class TestServidor {

    private static final int PORTA_VALIDA = 8080;

    @Mock
    private ServidorFactory servidorFactory;

    @Mock
    private ServerSocket serverSocket;

    @Mock
    private Socket socket1;

    @Mock
    private Socket socket2;

    @Mock
    private ServidorHandler handler1;

    @Mock
    private ServidorHandler handler2;

    @Captor
    private ArgumentCaptor<Set<PrintWriter>> escritoresCaptor;

    private PrintStream erroOriginal;
    private ByteArrayOutputStream erroCapturado;

    @BeforeEach
    void capturarSystemErr() {
        erroOriginal = System.err;
        erroCapturado = new ByteArrayOutputStream();
        System.setErr(new PrintStream(erroCapturado));
    }

    @AfterEach
    void restaurarSystemErr() {
        System.setErr(erroOriginal);
    }



    @Test
    void construtorDeveRejeitarFabricaNula() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Servidor(PORTA_VALIDA, null));
        assertEquals("A fábrica do servidor não pode ser nula.", ex.getMessage());
    }

    @Test
    void construtorDeveValidarPortaAntesDaFabrica() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Servidor(0, null));
        assertEquals("Porta inválida.", ex.getMessage());
    }

    @Test
    void servidorAtivoDeveCriarServidorComPortaInformada() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(true);
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        verify(servidorFactory, times(1)).criarServidor(PORTA_VALIDA);
        verifyNoMoreInteractions(servidorFactory);
    }

    @Test
    void servidorAtivoDeveTratarErroNaCriacaoDoServidor() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenThrow(new IOException("porta em uso"));
        Servidor servidor = new Servidor(PORTA_VALIDA, servidorFactory);
        assertDoesNotThrow(servidor::servidorAtivo);
        assertTrue(erroCapturado.toString().contains("Erro durante a execução do servidor: porta em uso"));
        verify(servidorFactory, never()).criarServidorHandler(any(), any(), any());
    }

    @Test
    void servidorAtivoDevePropagarRuntimeExceptionDaFabrica() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenThrow(new IllegalStateException("fábrica quebrada"));
        Servidor servidor = new Servidor(PORTA_VALIDA, servidorFactory);
        IllegalStateException ex = assertThrows(IllegalStateException.class, servidor::servidorAtivo);
        assertEquals("fábrica quebrada", ex.getMessage());
        assertEquals("", erroCapturado.toString());
    }

    @Test
    void servidorAtivoDeveLancarNullPointerQuandoFabricaRetornaServidorNulo() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(null);
        Servidor servidor = new Servidor(PORTA_VALIDA, servidorFactory);
        assertThrows(NullPointerException.class, servidor::servidorAtivo);
    }

    @Test
    void servidorAtivoNaoDeveAceitarSeSocketFechado() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(true);
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        verify(serverSocket, never()).accept();
        verify(servidorFactory, never()).criarServidorHandler(any(), any(), any());
        assertEquals("", erroCapturado.toString());
    }

    @Test
    void servidorAtivoDeveIniciarHandlerParaCadaConexao() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false, false, true);
        when(serverSocket.accept()).thenReturn(socket1, socket2);
        when(servidorFactory.criarServidorHandler(eq(socket1), any(), eq(servidorFactory))).thenReturn(handler1);
        when(servidorFactory.criarServidorHandler(eq(socket2), any(), eq(servidorFactory))).thenReturn(handler2);
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        verify(serverSocket, times(2)).accept();
        verify(handler1, times(1)).start();
        verify(handler2, times(1)).start();
    }

    @Test
    void servidorAtivoDeveSuportarMuitasConexoes() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false, false, false, false, false, true);
        when(serverSocket.accept()).thenReturn(socket1);
        when(servidorFactory.criarServidorHandler(any(), any(), any())).thenReturn(handler1);
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        verify(serverSocket, times(5)).accept();
        verify(handler1, times(5)).start();
    }

    @Test
    void servidorAtivoDeveCompartilharMesmoConjuntoEntreHandlers() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false, false, true);
        when(serverSocket.accept()).thenReturn(socket1, socket2);
        when(servidorFactory.criarServidorHandler(any(), any(), any())).thenReturn(handler1, handler2);
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        verify(servidorFactory, times(2)).criarServidorHandler(any(), escritoresCaptor.capture(), any());
        List<Set<PrintWriter>> conjuntos = escritoresCaptor.getAllValues();
        assertSame(conjuntos.get(0), conjuntos.get(1));
    }

    @Test
    void servidorAtivoDeveExporAlteracoesDoConjuntoAosProximosHandlers() throws Exception {
        PrintWriter escritor = new PrintWriter(new StringWriter());
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false, false, true);
        when(serverSocket.accept()).thenReturn(socket1, socket2);
        when(servidorFactory.criarServidorHandler(eq(socket1), any(), any())).thenAnswer(invocacao -> {
            Set<PrintWriter> conjunto = invocacao.getArgument(1);
            conjunto.add(escritor);
            return handler1;
        });
        when(servidorFactory.criarServidorHandler(eq(socket2), any(), any())).thenReturn(handler2);
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        verify(servidorFactory).criarServidorHandler(eq(socket2), escritoresCaptor.capture(), any());
        assertTrue(escritoresCaptor.getValue().contains(escritor));
        assertEquals(1, escritoresCaptor.getValue().size());
    }

    @Test
    void servidoresDistintosNaoDevemCompartilharEscritores() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false, true, false, true);
        when(serverSocket.accept()).thenReturn(socket1, socket2);
        when(servidorFactory.criarServidorHandler(any(), any(), any())).thenReturn(handler1, handler2);
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        verify(servidorFactory, times(2)).criarServidorHandler(any(), escritoresCaptor.capture(), any());
        List<Set<PrintWriter>> conjuntos = escritoresCaptor.getAllValues();
        assertNotSame(conjuntos.get(0), conjuntos.get(1));
    }

    @Test
    void servidorAtivoDeveExecutarOperacoesNaOrdemCorreta() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false, true);
        when(serverSocket.accept()).thenReturn(socket1);
        when(servidorFactory.criarServidorHandler(any(), any(), any())).thenReturn(handler1);
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        InOrder ordem = inOrder(servidorFactory, serverSocket, handler1);
        ordem.verify(servidorFactory).criarServidor(PORTA_VALIDA);
        ordem.verify(serverSocket).accept();
        ordem.verify(servidorFactory).criarServidorHandler(eq(socket1), any(), any());
        ordem.verify(handler1).start();
        ordem.verify(serverSocket).close();
    }

    @Test
    void servidorAtivoDeveFecharServerSocketAoTerminarLoop() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(true);
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        verify(serverSocket, times(1)).close();
    }

    @Test
    void servidorAtivoDeveTratarErroNoAccept() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false);
        when(serverSocket.accept()).thenThrow(new IOException("conexão recusada"));
        Servidor servidor = new Servidor(PORTA_VALIDA, servidorFactory);
        assertDoesNotThrow(servidor::servidorAtivo);
        assertTrue(erroCapturado.toString().contains("Erro durante a execução do servidor: conexão recusada"));
        verify(servidorFactory, never()).criarServidorHandler(any(), any(), any());
    }

    @Test
    void servidorAtivoDeveFecharServerSocketQuandoAcceptFalha() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false);
        when(serverSocket.accept()).thenThrow(new IOException("falha"));
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        verify(serverSocket, times(1)).close();
    }

    @Test
    void servidorAtivoDeveManterHandlersIniciadosAposFalhaPosterior() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false);
        when(serverSocket.accept()).thenReturn(socket1).thenThrow(new IOException("queda de rede"));
        when(servidorFactory.criarServidorHandler(eq(socket1), any(), any())).thenReturn(handler1);
        new Servidor(PORTA_VALIDA, servidorFactory).servidorAtivo();
        verify(handler1, times(1)).start();
        verify(serverSocket, times(2)).accept();
        assertTrue(erroCapturado.toString().contains("queda de rede"));
    }

    @Test
    void servidorAtivoDeveTratarErroAoFecharServerSocket() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(true);
        doThrow(new IOException("falha ao fechar")).when(serverSocket).close();
        Servidor servidor = new Servidor(PORTA_VALIDA, servidorFactory);
        assertDoesNotThrow(servidor::servidorAtivo);
        assertTrue(erroCapturado.toString().contains("Erro durante a execução do servidor: falha ao fechar"));
    }

    @Test
    void servidorAtivoDevePropagarRuntimeExceptionDoHandler() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false);
        when(serverSocket.accept()).thenReturn(socket1);
        when(servidorFactory.criarServidorHandler(any(), any(), any())).thenReturn(handler1);
        doThrow(new IllegalStateException("thread já iniciada")).when(handler1).start();
        Servidor servidor = new Servidor(PORTA_VALIDA, servidorFactory);
        IllegalStateException ex = assertThrows(IllegalStateException.class, servidor::servidorAtivo);
        assertEquals("thread já iniciada", ex.getMessage());
    }

    @Test
    void servidorAtivoDeveFecharServerSocketQuandoHandlerFalha() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false);
        when(serverSocket.accept()).thenReturn(socket1);
        when(servidorFactory.criarServidorHandler(any(), any(), any())).thenReturn(handler1);
        doThrow(new IllegalStateException("falha")).when(handler1).start();
        Servidor servidor = new Servidor(PORTA_VALIDA, servidorFactory);
        assertThrows(IllegalStateException.class, servidor::servidorAtivo);
        verify(serverSocket, times(1)).close();
    }

    @Test
    void servidorAtivoDevePropagarRuntimeExceptionAoCriarHandler() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false);
        when(serverSocket.accept()).thenReturn(socket1);
        when(servidorFactory.criarServidorHandler(any(), any(), any())).thenThrow(new IllegalArgumentException("socket inválido"));
        Servidor servidor = new Servidor(PORTA_VALIDA, servidorFactory);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, servidor::servidorAtivo);
        assertEquals("socket inválido", ex.getMessage());
        verify(serverSocket, times(1)).close();
    }

    @Test
    void servidorAtivoDeveFecharServerSocketQuandoHandlerNulo() throws Exception {
        when(servidorFactory.criarServidor(PORTA_VALIDA)).thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(false);
        when(serverSocket.accept()).thenReturn(socket1);
        when(servidorFactory.criarServidorHandler(any(), any(), any())).thenReturn(null);
        Servidor servidor = new Servidor(PORTA_VALIDA, servidorFactory);
        assertThrows(NullPointerException.class, servidor::servidorAtivo);
        verify(serverSocket, times(1)).close();
    }

    @Test
    void construtorDeveRejeitarPortaAcimaDoLimite() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Servidor(65536, servidorFactory)
        );

        assertEquals("Porta inválida.", ex.getMessage());
    }
}