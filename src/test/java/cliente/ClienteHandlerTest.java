package cliente;

import br.inatel.redesocial.cliente.Cliente;
import br.inatel.redesocial.cliente.ClienteFactory;
import br.inatel.redesocial.cliente.ClienteHandler;
import br.inatel.redesocial.cliente.ClienteSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClienteHandlerTest
{
    ClienteFactory clienteFactory;

    @BeforeEach
    public void mockarClienteFactory()
    {
        clienteFactory = Mockito.mock(ClienteFactory.class);
    }

    @Test
    public void testClienteFactoryNull()
    {
        assertThrows (IllegalArgumentException.class,() -> {
            ClienteHandler clienteHandler = new ClienteHandler(0,"test",null);
        });
    }

    @Test
    public void conectarDeveCriarClienteEConfigurarDados() throws  IOException {

        Socket socket = Mockito.mock(Socket.class);
        ClienteSocket clienteSocket = Mockito.mock(ClienteSocket.class);
        Cliente cliente = Mockito.mock(Cliente.class);

        Mockito.when(clienteFactory.criarSocket("localhost", 5000))
                .thenReturn(socket);

        Mockito.when(clienteFactory.criarClienteSocket(socket))
                .thenReturn(clienteSocket);

        Mockito.when(clienteFactory.criarCliente(clienteSocket))
                .thenReturn(cliente);

        ClienteHandler clienteHandler =
                new ClienteHandler(5000, "localhost", clienteFactory);

        Cliente resultado = clienteHandler.conectar(
                "teste@email.com",
                "usuario"
        );

        assertNotNull(resultado);

        Mockito.verify(clienteFactory).criarSocket("localhost", 5000);
        Mockito.verify(clienteFactory).criarClienteSocket(socket);
        Mockito.verify(clienteFactory).criarCliente(clienteSocket);

        Mockito.verify(cliente).setEmail("teste@email.com");
        Mockito.verify(cliente).setUsuario("usuario");
    }
    
}
