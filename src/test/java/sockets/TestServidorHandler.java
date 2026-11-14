package sockets;

import br.inatel.redesocial.servidor.GerenciadorSalas;
import br.inatel.redesocial.servidor.Mensagem;
import br.inatel.redesocial.servidor.Sala;
import br.inatel.redesocial.servidor.ServidorFactory;
import br.inatel.redesocial.servidor.ServidorHandler;
import br.inatel.redesocial.servidor.ServidorSocket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    @Mock
    private Sala sala;

    private MockedStatic<GerenciadorSalas> gerenciadorSalas;
    private ServidorHandler handler;

    @BeforeEach
    void criarHandler() throws Exception {
        gerenciadorSalas = mockStatic(GerenciadorSalas.class);
        handler = new ServidorHandler(socket, new HashSet<>(), servidorFactory);
        lenient().when(servidorFactory.criarServidorSocket(socket, servidorFactory)).thenReturn(servidorSocket);
        lenient().when(servidorSocket.getEscritor()).thenReturn(escritor);
        lenient().when(sala.getEscritores()).thenReturn(Set.of(escritor));
    }

    @AfterEach
    void fecharMockEstatico() {
        gerenciadorSalas.close();
    }

    private Mensagem msg(String type) {
        Mensagem m = new Mensagem();
        m.type = type;
        return m;
    }

    private Mensagem entrar(String usuario, String nomeSala) {
        Mensagem m = msg("entrar_sala");
        m.usuario = usuario;
        m.texto = nomeSala;
        return m;
    }

    private Mensagem texto(String t) {
        Mensagem m = msg("mensagem");
        m.texto = t;
        return m;
    }

    private void salaExiste(String nomeSala) {
        gerenciadorSalas.when(() -> GerenciadorSalas.getSala(nomeSala)).thenReturn(sala);
    }

    private void verificaErroEnviado() {
        verify(servidorSocket).enviar(argThat((Mensagem m) -> m != null && "erro".equals(m.type)));
    }

    @Test
    public void fechaSocketNoFinal() throws Exception {
        when(servidorSocket.receber()).thenReturn(null);
        handler.run();
        verify(socket).close();
    }

    @Test
    public void erroNaLeituraNaoLancaExcecaoEFechaSocket() throws Exception {
        when(servidorSocket.receber()).thenThrow(new IOException("falha"));
        assertDoesNotThrow(handler::run);
        verify(socket).close();
    }

    @Test
    public void jsonInvalidoEnviaErroEContinua() throws Exception {
        when(servidorSocket.receber())
                .thenThrow(new com.google.gson.JsonSyntaxException("x"))
                .thenReturn(null);
        handler.run();
        verificaErroEnviado();
    }

    @Test
    public void typeAusenteEnviaErro() throws Exception {
        when(servidorSocket.receber()).thenReturn(msg(null), (Mensagem) null);
        handler.run();
        verificaErroEnviado();
    }

    @Test
    public void comandoDesconhecidoEnviaErro() throws Exception {
        when(servidorSocket.receber()).thenReturn(msg("xpto"), (Mensagem) null);
        handler.run();
        verificaErroEnviado();
    }

    @Test
    public void salaInexistenteEnviaErro() throws Exception {
        when(servidorSocket.receber()).thenReturn(entrar("thiago", "naoExiste"), (Mensagem) null);
        handler.run();
        gerenciadorSalas.verify(() -> GerenciadorSalas.getSala("naoExiste"));
        verificaErroEnviado();
    }

    @Test
    public void entrarEmSalaAdicionaUsuarioComEscritor() throws Exception {
        salaExiste("geral");
        when(sala.addUsuario("thiago", escritor)).thenReturn(true);
        when(servidorSocket.receber())
                .thenReturn(entrar("thiago", "geral"), (Mensagem) null);

        handler.run();

        verify(sala).addUsuario("thiago", escritor);
    }
    @Test
    public void removeUsuarioEEscritorDaSalaNoFinal() throws Exception {
        salaExiste("geral");
        when(sala.addUsuario("thiago", escritor)).thenReturn(true);
        when(servidorSocket.receber()).thenReturn(entrar("thiago", "geral"), (Mensagem) null);
        handler.run();
        verify(sala).removeUsuario("thiago");
    }

    @Test
    public void semEntrarEmSalaNaoRemoveNada() throws Exception {
        when(servidorSocket.receber()).thenReturn((Mensagem) null);
        handler.run();
        verify(sala, never()).removeUsuario(any(String.class));
    }

    @Test
    public void criarSalaChamaGerenciadorComCapacidadeENome() throws Exception {
        Mensagem m = msg("criar_sala");
        m.sala = "nova";
        m.valorInt = 5;
        when(servidorSocket.receber()).thenReturn(m, (Mensagem) null);
        handler.run();
        gerenciadorSalas.verify(() -> GerenciadorSalas.addSala(5, "nova"));
    }

    @Test
    public void mensagemSemEstarEmSalaEnviaErro() throws Exception {
        when(servidorSocket.receber()).thenReturn(texto("oi"), (Mensagem) null);
        handler.run();
        verificaErroEnviado();
        verify(servidorSocket, never()).enviar(any(Mensagem.class), any(PrintWriter.class));
    }

    @Test
    public void mensagemNaSalaVaiParaOsEscritoresDaSala() throws Exception {
        salaExiste("geral");
        when(sala.addUsuario("thiago", escritor)).thenReturn(true);
        when(servidorSocket.receber())
                .thenReturn(entrar("thiago", "geral"), texto("oi"), (Mensagem) null);
        handler.run();
        verify(servidorSocket).enviar(
                argThat((Mensagem m) -> "thiago".equals(m.usuario) && "oi".equals(m.texto)),
                eq(escritor));
    }
    @Test
    public void mensagemComTextoNuloEnviaErro() throws Exception {
        Mensagem m = msg("mensagem");
        m.texto = null;
        when(servidorSocket.receber()).thenReturn(m, (Mensagem) null);
        handler.run();
        verificaErroEnviado();
    }

    @Test
    public void erroAoCriarServidorSocketFechaSocket() throws IOException {
        when(servidorFactory.criarServidorSocket(socket, servidorFactory)).thenThrow(new RuntimeException("falha"));
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, handler::run);
        verify(socket).close();
    }

    @Test
    public void salaLotadaEnviaErroENaoRemoveUsuario() throws Exception {
        salaExiste("geral");
        when(sala.addUsuario("thiago", escritor)).thenReturn(false);
        when(servidorSocket.receber()).thenReturn(entrar("thiago", "geral"), (Mensagem) null);
        handler.run();
        verificaErroEnviado();
        verify(sala, never()).removeUsuario(any(String.class));
    }

    @Test
    public void salaJaExistenteEnviaErroENaoAdicionaSala() throws Exception {
        gerenciadorSalas.when(() -> GerenciadorSalas.existeSala("geral")).thenReturn(true);
        Mensagem mensagem = msg("criar_sala");
        mensagem.sala = "geral";
        mensagem.valorInt = 5;
        when(servidorSocket.receber()).thenReturn(mensagem, (Mensagem) null);

        handler.run();

        verificaErroEnviado();
        gerenciadorSalas.verify(() -> GerenciadorSalas.addSala(5, "geral"), never());
    }

    @Test
    public void criarSalaComNomeNuloEnviaErro() throws Exception {
        gerenciadorSalas.when(() -> GerenciadorSalas.existeSala(null)).thenReturn(false);
        Mensagem mensagem = msg("criar_sala");
        mensagem.valorInt = 5;
        when(servidorSocket.receber()).thenReturn(mensagem, (Mensagem) null);

        handler.run();

        verificaErroEnviado();
        gerenciadorSalas.verify(() -> GerenciadorSalas.addSala(5, null), never());
    }

    @Test
    public void criarSalaComNomeEmBrancoEnviaErro() throws Exception {
        gerenciadorSalas.when(() -> GerenciadorSalas.existeSala("  ")).thenReturn(false);
        Mensagem mensagem = msg("criar_sala");
        mensagem.sala = "  ";
        mensagem.valorInt = 5;
        when(servidorSocket.receber()).thenReturn(mensagem, (Mensagem) null);

        handler.run();

        verificaErroEnviado();
        gerenciadorSalas.verify(() -> GerenciadorSalas.addSala(5, "  "), never());
    }

    @Test
    public void criarSalaComCapacidadeInvalidaEnviaErro() throws Exception {
        gerenciadorSalas.when(() -> GerenciadorSalas.existeSala("geral")).thenReturn(false);
        Mensagem mensagem = msg("criar_sala");
        mensagem.sala = "geral";
        mensagem.valorInt = 0;
        when(servidorSocket.receber()).thenReturn(mensagem, (Mensagem) null);

        handler.run();

        verificaErroEnviado();
        gerenciadorSalas.verify(() -> GerenciadorSalas.addSala(0, "geral"), never());
    }

    @Test
    public void mensagemComTextoNuloNaSalaEnviaErroSemEncaminhar() throws Exception {
        salaExiste("geral");
        when(sala.addUsuario("thiago", escritor)).thenReturn(true);
        Mensagem mensagemNula = texto(null);
        when(servidorSocket.receber())
                .thenReturn(entrar("thiago", "geral"), mensagemNula, (Mensagem) null);

        handler.run();

        verificaErroEnviado();
        verify(servidorSocket, never()).enviar(
                argThat((Mensagem enviada) -> enviada != null && "mensagem".equals(enviada.type)),
                eq(escritor));
    }

    @Test
    public void erroAoFecharSocketNaoEscapa() throws Exception {
        when(servidorSocket.receber()).thenReturn((Mensagem) null);
        doThrow(new IOException("falha ao fechar")).when(socket).close();

        assertDoesNotThrow(handler::run);

        verify(socket).close();
    }

    @Test
    public void saidaDaSalaNotificaUsuariosEAtualizaPresenca() throws Exception {
        salaExiste("geral");

        when(sala.addUsuario("thiago", escritor)).thenReturn(true);
        when(sala.getQuantidadeUsuarios()).thenReturn(1, 0);

        when(servidorSocket.receber())
                .thenReturn(entrar("thiago", "geral"), (Mensagem) null);

        handler.run();

        verify(sala).removeUsuario("thiago");

        ArgumentCaptor<Mensagem> captor =
                ArgumentCaptor.forClass(Mensagem.class);

        verify(servidorSocket, times(4))
                .enviar(captor.capture(), eq(escritor));

        List<Mensagem> mensagens = captor.getAllValues();

        assertTrue(mensagens.stream()
                .anyMatch(m -> "presenca".equals(m.type)));

        verify(socket).close();
    }

    @Test
    public void entradaBemSucedidaEnviaConfirmacaoComQuantidadeDeUsuarios()
            throws Exception {

        salaExiste("geral");

        when(sala.addUsuario("thiago", escritor)).thenReturn(true);
        when(sala.getQuantidadeUsuarios()).thenReturn(1);

        when(servidorSocket.receber())
                .thenReturn(entrar("thiago", "geral"), (Mensagem) null);

        handler.run();

        ArgumentCaptor<Mensagem> captor =
                ArgumentCaptor.forClass(Mensagem.class);

        verify(servidorSocket).enviar(captor.capture());

        Mensagem enviada = captor.getValue();

        assertEquals("entrada_confirmada", enviada.type);
        assertEquals(1, enviada.valorInt);

        verify(sala, atLeastOnce()).getNomesUsuariosAtivos();
    }

}