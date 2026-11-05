package CRUDTestes;

import br.inatel.redesocial.autenticacao.Login;
import br.inatel.redesocial.banco.funcoes.LoginDAO;
import br.inatel.redesocial.autenticacao.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LoginTest {
    private Login login;
    private final String email = "christopher@inatel.br";
    private final String senha = "Chizinho67!";
    private final String nome = "Chris_67";

    @Mock
    private LoginDAO loginDAO;

    @BeforeEach
    public void setup(){
        this.login = new Login(loginDAO);
    }

    @Test
    public void testeAutentificar(){
        Usuario usuario = new Usuario(email, senha, nome);
        Mockito.when(loginDAO.auteticaUser(email, senha)).thenReturn(true);

        boolean autenticado = login.autenticarUsuario(usuario, senha);

        assertTrue(autenticado);
        assertTrue(usuario.getLogado());
    }

    @Test
    public void testeAutenticarInvalido(){
        String senhaInvalida = "senha123";
        Usuario usuario = new Usuario(email, senha, nome);
        Mockito.when(loginDAO.auteticaUser(email, senhaInvalida)).thenReturn(false);

        boolean autenticado = login.autenticarUsuario(usuario, senhaInvalida);

        assertFalse(autenticado);
        assertFalse(usuario.getLogado());
    }

    @Test
    public void testeAutenticarUsuarioNulo(){
        assertThrows(NullPointerException.class, ()->login.autenticarUsuario(null, senha));
        Mockito.verifyNoInteractions(loginDAO);
    }

    @Test
    public void testeAutenticarUsuarioFalhaEMandaException(){
        Usuario usuario = new Usuario(email, senha, nome);

        Mockito.when(loginDAO.auteticaUser(email, senha)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, ()-> login.autenticarUsuario(usuario, senha));
    }

    @Test
    public void testeVerificarEmail(){
        boolean emailTeste = Login.verificarEmail(email);

        assertTrue(emailTeste);
    }

    @Test
    public void testeVerificarEmailInvalido(){
        boolean emailTeste = Login.verificarEmail("@inatel.br");

        assertFalse(emailTeste);
    }

    @Test
    public void testeVerificarSenha(){
        boolean senhaTeste = Login.verificarSenha(senha);

        assertTrue(senhaTeste);
    }

    @Test
    public void testeVerificarSenhaInvalida(){
        boolean senhaTeste = Login.verificarSenha("1234");

        assertFalse(senhaTeste);
    }

    @Test
    public void testeVerificarNome(){
        boolean nomeTeste = Login.verificarNome(nome);

        assertTrue(nomeTeste);
    }

    @Test
    public void testeVerificarNomeInvalido(){
        boolean nomeTeste = Login.verificarNome("Chris de Souza");

        assertFalse(nomeTeste);
    }
}
