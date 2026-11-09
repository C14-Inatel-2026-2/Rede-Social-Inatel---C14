package CRUDTestes;

import br.inatel.redesocial.banco.funcoes.UsuarioDAO;
import br.inatel.redesocial.usuario.GerenciadorUsuariosBD;
import br.inatel.redesocial.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
public class GerenciadorUsuariosBDTest {
    GerenciadorUsuariosBD gerenciador;

    private final String email = "christopher@inatel.br";

    @Mock
    UsuarioDAO usuarioDAO;

    @BeforeEach
    public void setup (){
        this.gerenciador = new GerenciadorUsuariosBD(usuarioDAO);
    }

    @Test
    public void testeBuscaUsuarioExistente(){
        Usuario esperado = new Usuario(email, null);

        Mockito.when(usuarioDAO.buscarPorEmail(email)).thenReturn(esperado);

        assertSame(esperado, gerenciador.buscaUsuarioPorEmail(email));
    }
}
