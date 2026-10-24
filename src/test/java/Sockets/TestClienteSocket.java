package Sockets;
import br.inatel.redesocial.cliente.ClienteFactory;
import br.inatel.redesocial.cliente.ClienteSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


public class TestClienteSocket {

    Socket socket ;
    @BeforeEach
    public  void inicializarSocket ()
    {
        socket = Mockito.mock(Socket.class);
    }

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
        when(socket.isClosed()).thenReturn(true);
        assertThrows(IOException.class,() ->{
            ClienteSocket clienteSocket = new ClienteSocket(socket);
        });
    }

    @Test
    public void testsocketconected ()
    {
        when(socket.isConnected()).thenReturn(false);
        assertThrows(IOException.class,() ->{
            ClienteSocket clienteSocket = new ClienteSocket(socket);
        });
    }

}
