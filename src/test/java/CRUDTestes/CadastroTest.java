package CRUDTestes;

import br.inatel.redesocial.autenticacao.Cadastro;
import br.inatel.redesocial.autenticacao.Login;
import br.inatel.redesocial.autenticacao.Usuario;
import br.inatel.redesocial.banco.funcoes.CadastroDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CadastroTest {

    Cadastro cadastro;

    private String emailValido = "christopher@inatel.br";
    private String senhaValida = "Chizinho67!";
    private String emailInvalido = "@inatel.br";
    private String senhaInvalida = "senha123";
    private String emailNulo = "";
    private String senhaNula = "";

    @Mock
    Login login;
    @Mock
    CadastroDAO cadastroDAO;

    @BeforeEach
    public void setup() {
        this.cadastro = new Cadastro(cadastroDAO);
    }

    @Test
    public void testeSucessoNoCadastro(){
        Usuario usuarioCadastrado;

        when(cadastroDAO.insertUsuario(emailValido, senhaValida)).thenReturn(true);

        usuarioCadastrado = cadastro.criarUsuario(emailValido, senhaValida, login);

        assertNotNull(usuarioCadastrado);
        assertEquals(emailValido, usuarioCadastrado.getEmail());
        verify(cadastroDAO).insertUsuario(emailValido, senhaValida);
    }
}
