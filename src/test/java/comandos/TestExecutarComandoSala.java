package comandos;

import br.inatel.redesocial.cliente.Cliente;
import br.inatel.redesocial.cliente.comandos.ExecutarComandoSala;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ResourceLock(Resources.SYSTEM_OUT)
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

    @Test
    void deveMudarParaSalaCadastrada() {

        String sala = "geral";

        when(cliente.getSala()).thenReturn(Set.of(sala));

        ExecutarComandoSala.executar(
                "*mudar_sala",
                sala,
                cliente
        );

        verify(cliente).getSala();
        verify(cliente).setSalaAtiva(sala);
        verify(cliente, never()).addSala(anyString());
    }

    @Test
    void naoDeveMudarParaSalaNaoCadastrada() {

        String sala = "privada";

        when(cliente.getSala()).thenReturn(Set.of("geral"));

        ByteArrayOutputStream saida =
                new ByteArrayOutputStream();

        PrintStream saidaOriginal = System.out;

        try {
            System.setOut(new PrintStream(saida));

            ExecutarComandoSala.executar(
                    "*mudar_sala",
                    sala,
                    cliente
            );

        } finally {
            System.setOut(saidaOriginal);
        }

        assertEquals(
                "Você não está cadastrado nesta sala!",
                saida.toString().trim()
        );

        verify(cliente).getSala();
        verify(cliente, never()).setSalaAtiva(anyString());
    }

    @Test
    void naoDeveExecutarAcaoParaComandoDesconhecido() {

        ExecutarComandoSala.executar(
                "*comando_invalido",
                "geral",
                cliente
        );

        verifyNoInteractions(cliente);
    }
}
