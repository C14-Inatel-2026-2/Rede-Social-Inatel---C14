package Sockets;

import br.inatel.redesocial.servidor.Servidor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestServidorSocket {
    @Test
    public void portaMenorValida(){
        assertThrows(IllegalArgumentException.class, () ->{
            new Servidor(-1);
        });
    }
    @Test
    public void portaMaiorValida(){
        assertThrows(IllegalArgumentException.class, () ->{
            new Servidor(65536);
        });
    }
}
