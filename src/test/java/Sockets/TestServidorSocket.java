package Sockets;

import br.inatel.redesocial.servidor.Servidor;
import br.inatel.redesocial.servidor.ServidorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestServidorSocket {

    @Mock
    private ServidorFactory servidorFactory;

    @Test
    public void portaMenorInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Servidor(-1, servidorFactory);
        });
    }

    @Test
    public void portaMaiorInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Servidor(65536, servidorFactory);
        });
    }
}