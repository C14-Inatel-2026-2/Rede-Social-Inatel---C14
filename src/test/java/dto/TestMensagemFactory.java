
package dto;

import br.inatel.redesocial.cliente.Cliente;
import br.inatel.redesocial.cliente.dto.MensagemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TestMensagemFactory {

    Cliente cliente;

    @BeforeEach
    public void executar() {
        cliente = Mockito.mock(Cliente.class);

        when(cliente.getSalaAtiva()).thenReturn("sala");
        when(cliente.getUsuario()).thenReturn("hero");
        when(cliente.getEmail()).thenReturn("@");
    }

    @Test
    public void comandoComAsterisco() {

        var mensagem = MensagemFactory.criar("*io", "uou", cliente);

        assertEquals("io", mensagem.type);
        assertEquals("uou", mensagem.texto);
        assertEquals("sala", mensagem.sala);
        assertEquals("hero", mensagem.usuario);
        assertEquals("@", mensagem.email);
    }

    @Test
    public void comandoSemAsterisco() {

        var mensagem = MensagemFactory.criar("io", "uou", cliente);

        assertEquals("io", mensagem.type);
        assertEquals("uou", mensagem.texto);
        assertEquals("sala", mensagem.sala);
        assertEquals("hero", mensagem.usuario);
        assertEquals("@", mensagem.email);
    }
}
