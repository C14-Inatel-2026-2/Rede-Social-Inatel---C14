package Cliente;
import br.inatel.redesocial.cliente.Cliente;
import br.inatel.redesocial.cliente.ClienteSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class TestCliente
{
     ClienteSocket clienteSocket;

    @BeforeEach
    public  void configurarSocket() throws IOException
    {
        clienteSocket = Mockito.mock(ClienteSocket.class);
    }
    @Test
    public void testClienteSocketNull ()
    {
        assertThrows(IllegalArgumentException.class,() ->{
            Cliente cliente = new Cliente(null);
        });
    }

    @Test
    public  void testLoopReceberMensagem() throws IOException
    {
        Cliente cliente = new Cliente(clienteSocket);

       when(clienteSocket.ReceberMensagem()).thenReturn(null);
       cliente.receberMensagem();

       verify(clienteSocket, times(1)).ReceberMensagem();
    }

}
