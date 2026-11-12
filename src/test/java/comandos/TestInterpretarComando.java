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

  
}
