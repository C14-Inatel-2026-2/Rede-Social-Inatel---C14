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


}
