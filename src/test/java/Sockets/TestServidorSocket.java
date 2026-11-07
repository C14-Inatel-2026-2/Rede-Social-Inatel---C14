package Sockets;

import br.inatel.redesocial.servidor.Mensagem;
import br.inatel.redesocial.servidor.ServidorFactory;
import br.inatel.redesocial.servidor.ServidorSocket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestServidorSocket{

    @Mock
    private ServidorFactory factory;
    @Mock
    private Socket socket;

    private StringWriter saida;

    private ServidorSocket criar(String entrada) throws IOException {
        saida = new StringWriter();
        when(factory.criarPrintWriter(socket)).thenReturn(new PrintWriter(saida, true));
        when(factory.criarBufferReader(socket)).thenReturn(new BufferedReader(new StringReader(entrada)));
        return new ServidorSocket(socket, factory);
    }

    @Test
    public void leituraDevolveUmaLinha() throws IOException {
        ServidorSocket s = criar("ola\n");
        assertEquals("ola", s.leitura());
    }

    @Test
    public void receberConverteJsonEmMensagem() throws IOException {
        ServidorSocket s = criar("{\"type\":\"mensagem\",\"texto\":\"oi\"}\n");
        Mensagem m = s.receber();
        assertEquals("mensagem", m.type);
        assertEquals("oi", m.texto);
    }

    @Test
    public void receberDevolveNullQuandoConexaoFecha() throws IOException {
        ServidorSocket s = criar("");
        assertNull(s.receber());
    }

    @Test
    public void receberIgnoraJsonInvalidoEAvisaCliente() throws IOException {
        ServidorSocket s = criar("{quebrado\n{\"type\":\"mensagem\",\"texto\":\"oi\"}\n");
        Mensagem m = s.receber();
        assertEquals("oi", m.texto);
        assertTrue(saida.toString().contains("erro"));
    }

    @Test
    public void receberIgnoraMensagemSemType() throws IOException {
        ServidorSocket s = criar("{\"texto\":\"oi\"}\n{\"type\":\"mensagem\",\"texto\":\"x\"}\n");
        assertEquals("x", s.receber().texto);
        assertTrue(saida.toString().contains("erro"));
    }

    @Test
    public void receberDevolveNullNoComandoSair() throws IOException {
        ServidorSocket s = criar("{\"type\":\"sair\"}\n");
        assertNull(s.receber());
    }

    @Test
    public void enviarEscreveJsonEmUmaLinha() throws IOException {
        ServidorSocket s = criar("");
        s.enviar(Mensagem.erro("falhou"));
        String linha = saida.toString().trim();
        assertTrue(linha.startsWith("{") && linha.contains("falhou"));
        assertEquals(1, saida.toString().lines().count());
    }

    @Test
    public void enviarParaDestinoEscreveUsuarioETexto() throws IOException {
        ServidorSocket s = criar("");
        StringWriter outro = new StringWriter();
        Mensagem m = new Mensagem();
        m.usuario = "thiago";
        m.texto = "oi";
        s.enviarTexto(m, new PrintWriter(outro, true));
        assertEquals("thiago: oi", outro.toString().trim());
    }
}