package comandos;

import br.inatel.redesocial.cliente.Cliente;
import br.inatel.redesocial.cliente.comandos.IdentificarComandoChat;
import br.inatel.redesocial.cliente.dto.MensagemCliente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestIdentificarComandoChat {

    @Mock
    private Cliente cliente;

    @BeforeEach
    void configurarCliente() {
        when(cliente.getUsuario()).thenReturn("Leonardo");
        when(cliente.getEmail()).thenReturn("leo@email.com");
        when(cliente.getSalaAtiva()).thenReturn("geral");
    }

    @Test
    void deveIdentificarMensagemComum() {

        MensagemCliente resultado =
                IdentificarComandoChat.identificarComando(
                        "Olá pessoal!", cliente);

        assertEquals("mensagem", resultado.type);
        assertEquals("Olá pessoal!", resultado.texto);
        assertEquals("geral", resultado.sala);
        assertEquals("Leonardo", resultado.usuario);
        assertEquals("leo@email.com", resultado.email);
    }

    @Test
    void deveIdentificarMensagemComEspacos() {

        MensagemCliente resultado =
                IdentificarComandoChat.identificarComando(
                        "   Olá pessoal!   ", cliente);

        assertEquals("mensagem", resultado.type);
        assertEquals("Olá pessoal!", resultado.texto);
    }

    @Test
    void deveIdentificarComandoEntrarSala() {

        MensagemCliente resultado =
                IdentificarComandoChat.identificarComando(
                        "*entrar_sala -> tecnologia", cliente);

        verify(cliente).addSala("tecnologia");
        verify(cliente).setSalaAtiva("tecnologia");

        assertEquals("entrar_sala", resultado.type);
        assertEquals("tecnologia", resultado.texto);
        assertEquals("Leonardo", resultado.usuario);
        assertEquals("leo@email.com", resultado.email);
    }

    @Test
    void deveMudarSalaQuandoUsuarioJaEstaCadastrado() {

        when(cliente.getSala()).thenReturn(
                Set.of("geral", "tecnologia"));

        MensagemCliente resultado =
                IdentificarComandoChat.identificarComando(
                        "*mudar_sala -> tecnologia", cliente);

        verify(cliente).setSalaAtiva("tecnologia");
        verify(cliente, never()).addSala(anyString());

        assertEquals("mudar_sala", resultado.type);
        assertEquals("tecnologia", resultado.texto);
    }

    @Test
    void naoDeveMudarSalaQuandoUsuarioNaoEstaCadastrado() {

        when(cliente.getSala()).thenReturn(
                Set.of("geral", "tecnologia"));

        MensagemCliente resultado =
                IdentificarComandoChat.identificarComando(
                        "*mudar_sala -> jogos", cliente);

        verify(cliente, never()).setSalaAtiva("jogos");
        verify(cliente, never()).addSala(anyString());

        assertEquals("mudar_sala", resultado.type);
        assertEquals("jogos", resultado.texto);
    }

 
}
