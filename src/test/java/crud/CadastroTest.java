package crud;

import br.inatel.redesocial.usuario.Cadastro;
import br.inatel.redesocial.usuario.Usuario;
import br.inatel.redesocial.banco.funcoes.CadastroDAO;
import br.inatel.redesocial.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CadastroTest {

    Cadastro cadastro;

    private final String emailValido = "christopher@inatel.br";
    private final String senhaValida = "Chizinho67!";
    private final String nomeValido = "Christopher";
    private final String emailInvalido = "@inatel.br";
    private final String senhaInvalida = "senha123";
    private final String nomeInvalido = "Chris de Souza";
    private final String emailVazio = "";
    private final String senhaVazia = "";
    private final String nomeVazio = "";

    @Mock
    CadastroDAO cadastroDAO;

    @BeforeEach
    public void setup() {
        this.cadastro = new Cadastro(cadastroDAO);
    }

    @Test
    public void testeSucessoNoCadastro(){
        Usuario usuarioCadastrado;

        when(cadastroDAO.insertUsuario(emailValido, senhaValida, nomeValido)).thenReturn(true);

        usuarioCadastrado = cadastro.criarUsuario(emailValido, senhaValida, nomeValido);

        assertNotNull(usuarioCadastrado);
        assertEquals(emailValido, usuarioCadastrado.getEmail());
        verify(cadastroDAO).insertUsuario(emailValido, senhaValida, nomeValido);
    }

    @Test
    public void testeFalhaNoCadastroPorEmailInvalido(){
        assertThrows(EmailInvalidoException.class,
                () -> cadastro.criarUsuario(emailInvalido, senhaValida, nomeValido));
    }

    @Test
    public void testeFalhaNoCadastroPorSenhaInvalida() {
        assertThrows(SenhaInvalidaException.class,
                () -> cadastro.criarUsuario(emailValido, senhaInvalida, nomeValido));
    }

    @Test
    public void testeFalhaNoCadastroPorEmailNaoInformado(){
        assertThrows(CampoObrigatorioException.class,
                () -> cadastro.criarUsuario(emailVazio, senhaValida, nomeValido));
    }

    @Test
    public void testeFalhaNoCadastroPorEmailNulo(){
        assertThrows(CampoObrigatorioException.class,
                () -> cadastro.criarUsuario(null, senhaValida, nomeValido));
    }

    @Test
    public void testeFalhaNoCadastroPorSenhaNaoInformada() {
        assertThrows(CampoObrigatorioException.class,
                () -> cadastro.criarUsuario(emailValido, senhaVazia, nomeValido));
    }

    @Test
    public void testeFalhaNoCadastroPorSenhaNula() {
        assertThrows(CampoObrigatorioException.class,
                () -> cadastro.criarUsuario(emailValido, null, nomeValido));
    }

    @Test
    public void testeFalhaNoCadastroPorNomeNaoInformado() {
        assertThrows(CampoObrigatorioException.class,
                () -> cadastro.criarUsuario(emailValido, senhaValida, nomeVazio));
    }

    @Test
    public void testeFalhaNoCadastroPorNomeNulo() {
        assertThrows(CampoObrigatorioException.class,
                () -> cadastro.criarUsuario(emailValido, senhaValida, null));
    }

    @Test
    public void testeFalhaNoCadastroPorNomeInvalido(){
        assertThrows(NomeInvalidoException.class,
                () -> cadastro.criarUsuario(emailValido, senhaValida, nomeInvalido));
    }

    @Test
    public void testeEmailDuplicado(){
        when(cadastroDAO.insertUsuario(emailValido, senhaValida, nomeValido)).thenThrow(
                new EmailJaCadastradoException("E-mail já está em uso"));

        assertThrows(EmailJaCadastradoException.class,
                () -> cadastro.criarUsuario(emailValido, senhaValida, nomeValido));

    }

    @Test
    public void testeFalhaNoBanco(){
        when(cadastroDAO.insertUsuario(emailValido, senhaValida, nomeValido)).thenReturn(false);

        assertThrows(FalhaNoCadastroException.class,
                () -> cadastro.criarUsuario(emailValido, senhaValida, nomeValido));
    }
}
