package comandos;

import br.inatel.redesocial.cliente.Cliente;
import br.inatel.redesocial.cliente.comandos.ExecutarComandoSala;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestExecutarComandoSala {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = mock(Cliente.class);
    }

    @Test
    void deveEntrarNaSala() {

        String sala = "geral";

        ExecutarComandoSala.executar(
                "*entrar_sala",
                sala,
                cliente
        );

        verify(cliente).addSala(sala);
        verify(cliente).setSalaAtiva(sala);
    }


}
