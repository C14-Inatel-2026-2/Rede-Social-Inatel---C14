package Cliente;

import br.inatel.redesocial.cliente.ClienteFactory;
import br.inatel.redesocial.cliente.ClienteHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
    
}
