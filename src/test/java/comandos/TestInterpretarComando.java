package comandos;

import br.inatel.redesocial.cliente.comandos.InterpretarComando;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class TestInterpretarComando {

    @Test
    void deveInterpretarComandoComSeparador() {

        String msg = "*entrar_sala -> geral";

        String[] resultado = InterpretarComando.interpretar(msg);

        assertArrayEquals(
                new String[]{"*entrar_sala", "geral"},
                resultado
        );
    }

    @Test
    void deveRemoverEspacosDesnecessarios() {

        String msg = "  *mudar_sala  ->  geral  ";

        String[] resultado = InterpretarComando.interpretar(msg);

        assertArrayEquals(
                new String[]{"*mudar_sala", "geral"},
                resultado
        );
    }

    @Test
    void deveInterpretarMensagemSemSeparador() {

        String msg = "Olá, tudo bem?";

        String[] resultado = InterpretarComando.interpretar(msg);

        assertArrayEquals(
                new String[]{"mensagem", "Olá, tudo bem?"},
                resultado
        );
    }
}
