package Sockets;
import br.inatel.redesocial.cliente.ClienteSocket;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class TestClienteSocket {

    @Test
    public void testsocketnull ()
    {
        assertThrows(IllegalArgumentException.class,() ->{
            ClienteSocket clienteSocket = new ClienteSocket(null);
        });
    }

    @Test
    public void testsocketclose ()
    {
        MockSocket socket = new MockSocket();
        assertThrows(IOException.class,() ->{
            ClienteSocket clienteSocket = new ClienteSocket(socket);
        });
    }

    @Test
    public void testsocketconected ()
    {
        MockSocket socket = new MockSocket();
        socket.fechado = false ;
        assertThrows(IOException.class,() ->{
            ClienteSocket clienteSocket = new ClienteSocket(socket);
        });
    }

}
