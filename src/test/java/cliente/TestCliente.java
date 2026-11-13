package cliente;

import br.inatel.redesocial.cliente.Cliente;
import br.inatel.redesocial.cliente.ClienteSocket;
import br.inatel.redesocial.cliente.comandos.IdentificarComandoChat;
import br.inatel.redesocial.cliente.dto.MensagemCliente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;
import java.util.Scanner;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestCliente {

    ClienteSocket clienteSocket;
    Cliente cliente;

    @BeforeEach
    public void configurarSocket() throws IOException {
        clienteSocket = Mockito.mock(ClienteSocket.class);
        cliente = new Cliente(clienteSocket);
    }

    // CONSTRUTOR

    @Test
    public void testClienteSocketNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Cliente(null);
        });
    }

    @Test
    public void testCriarClienteComSocketValido() {
        assertNotNull(cliente);
        assertSame(clienteSocket, cliente.getClienteSocket());
    }

    // ATRIBUTOS

    @Test
    public void testDefinirEmail() {
        cliente.setEmail("teste@email.com");

        assertEquals("teste@email.com", cliente.getEmail());
    }

    @Test
    public void testDefinirUsuario() {
        cliente.setUsuario("Leonardo");

        assertEquals("Leonardo", cliente.getUsuario());
    }

    @Test
    public void testDefinirSalaAtiva() {
        cliente.setSalaAtiva("geral");

        assertEquals("geral", cliente.getSalaAtiva());
    }

    @Test
    public void testSalaAtivaInicialmenteVazia() {
        assertEquals("", cliente.getSalaAtiva());
    }

    // GERENCIAMENTO DE SALAS

    @Test
    public void testAdicionarSala() {
        cliente.addSala("geral");

        assertTrue(cliente.getSala().contains("geral"));
    }

    @Test
    public void testRemoverSala() {
        cliente.addSala("geral");

        cliente.removeSala("geral");

        assertFalse(cliente.getSala().contains("geral"));
    }

    @Test
    public void testNaoDuplicarSalas() {
        cliente.addSala("geral");
        cliente.addSala("geral");

        assertEquals(1, cliente.getSala().size());
    }

    @Test
    public void testManterOrdemInsercaoSalas() {
        cliente.addSala("geral");
        cliente.addSala("jogos");
        cliente.addSala("estudos");

        assertEquals(
                new LinkedHashSet<>(java.util.List.of(
                        "geral", "jogos", "estudos"
                )),
                cliente.getSala()
        );
    }

    @Test
    public void testSalaInicialmenteVazia() {
        assertNotNull(cliente.getSala());
        assertTrue(cliente.getSala().isEmpty());
    }

    // RECEBIMENTO DE MENSAGENS

    @Test
    public void testLoopReceberMensagem() throws IOException {
        when(clienteSocket.ReceberMensagem()).thenReturn(null);

        cliente.receberMensagem();

        verify(clienteSocket, times(1)).ReceberMensagem();
    }

    @Test
    public void testEntregarMensagemAoOuvinte() throws IOException {
        MensagemCliente mensagem = mock(MensagemCliente.class);

        when(clienteSocket.ReceberMensagem())
                .thenReturn(mensagem, (MensagemCliente) null);

        AtomicReference<MensagemCliente> mensagemRecebida =
                new AtomicReference<>();

        cliente.setOuvinte(mensagemRecebida::set);

        cliente.receberMensagem();

        assertSame(mensagem, mensagemRecebida.get());

        verify(clienteSocket, times(2)).ReceberMensagem();
    }

    @Test
    public void testReceberMultiplasMensagens() throws IOException {
        MensagemCliente mensagem1 = mock(MensagemCliente.class);
        MensagemCliente mensagem2 = mock(MensagemCliente.class);

        when(clienteSocket.ReceberMensagem())
                .thenReturn(
                        mensagem1,
                        mensagem2,
                        (MensagemCliente) null
                );

        AtomicReference<MensagemCliente> ultimaMensagem =
                new AtomicReference<>();

        cliente.setOuvinte(ultimaMensagem::set);

        cliente.receberMensagem();

        assertSame(mensagem2, ultimaMensagem.get());

        verify(clienteSocket, times(3)).ReceberMensagem();
    }

    @Test
    public void testImprimirMensagemSemOuvinte() throws IOException {
        MensagemCliente mensagem = new MensagemCliente();
        mensagem.texto = "Olá, mundo!";

        when(clienteSocket.ReceberMensagem())
                .thenReturn(mensagem, (MensagemCliente) null);

        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream saidaCapturada =
                new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(saidaCapturada));

            cliente.receberMensagem();

        } finally {
            System.setOut(saidaOriginal);
        }

        assertTrue(saidaCapturada.toString().contains("Olá, mundo!"));
    }

    // ENVIO DE MENSAGENS

    @Test
    public void testSaidaEnviarMensagem() throws IOException {
        MensagemCliente mensagemEsperada = mock(MensagemCliente.class);

        try (MockedStatic<IdentificarComandoChat> identificarMock =
                     Mockito.mockStatic(IdentificarComandoChat.class)) {

            identificarMock.when(() ->
                    IdentificarComandoChat.identificarComando(
                            "saida", cliente
                    )
            ).thenReturn(mensagemEsperada);

            cliente.enviarMensagem(new Scanner("saida\n"), cliente);

            identificarMock.verify(() ->
                    IdentificarComandoChat.identificarComando(
                            "saida", cliente
                    )
            );

            verify(clienteSocket, times(1))
                    .EnviarMensagem(mensagemEsperada);
        }
    }

    @Test
    public void testEnviarMultiplasMensagens() throws IOException {
        MensagemCliente mensagem1 = mock(MensagemCliente.class);
        MensagemCliente mensagem2 = mock(MensagemCliente.class);
        MensagemCliente mensagemSaida = mock(MensagemCliente.class);

        try (MockedStatic<IdentificarComandoChat> identificarMock =
                     Mockito.mockStatic(IdentificarComandoChat.class)) {

            identificarMock.when(() ->
                    IdentificarComandoChat.identificarComando(
                            "oi", cliente
                    )
            ).thenReturn(mensagem1);

            identificarMock.when(() ->
                    IdentificarComandoChat.identificarComando(
                            "hello", cliente
                    )
            ).thenReturn(mensagem2);

            identificarMock.when(() ->
                    IdentificarComandoChat.identificarComando(
                            "saida", cliente
                    )
            ).thenReturn(mensagemSaida);

            String mensagemEnviada = "oi\nhello\nsaida\n";

            cliente.enviarMensagem(
                    new Scanner(mensagemEnviada),
                    cliente
            );

            identificarMock.verify(() ->
                    IdentificarComandoChat.identificarComando(
                            "oi", cliente
                    )
            );

            identificarMock.verify(() ->
                    IdentificarComandoChat.identificarComando(
                            "hello", cliente
                    )
            );

            identificarMock.verify(() ->
                    IdentificarComandoChat.identificarComando(
                            "saida", cliente
                    )
            );

            verify(clienteSocket).EnviarMensagem(mensagem1);
            verify(clienteSocket).EnviarMensagem(mensagem2);
            verify(clienteSocket).EnviarMensagem(mensagemSaida);

            verify(clienteSocket, times(3))
                    .EnviarMensagem(any(MensagemCliente.class));
        }
    }

    // RUN

    @Test
    public void testRunIniciarRecebimento() throws IOException {
        when(clienteSocket.ReceberMensagem()).thenReturn(null);

        assertDoesNotThrow(() -> cliente.run());

        verify(clienteSocket, times(1)).ReceberMensagem();
    }

    @Test
    public void testRunConverterIOExceptionEmRuntimeException()
            throws IOException {

        when(clienteSocket.ReceberMensagem())
                .thenThrow(new IOException("Erro de conexão"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cliente.run()
        );

        assertInstanceOf(IOException.class, exception.getCause());

        assertEquals(
                "Erro de conexão",
                exception.getCause().getMessage()
        );
    }
}

