package crud;

import br.inatel.redesocial.banco.funcoes.UsuarioDAO;
import br.inatel.redesocial.exceptions.CampoObrigatorioException;
import br.inatel.redesocial.exceptions.SenhaInvalidaException;
import br.inatel.redesocial.usuario.TrocarSenha;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TrocarSenhaTest {
    TrocarSenha trocaSenha;

    private final String senhaValida = "Chizinho67!";
    private final String emailValido = "christopher@inatel.br";

    @Mock
    UsuarioDAO usuarioDAO;

    @BeforeEach
    public void setup(){
        this.trocaSenha = new TrocarSenha(usuarioDAO);
    }

    @Test
    public void testeAlterarSenha(){
        boolean sucessoAlteracao;
        String senhaNovaValida = "Theofficetop1!";

        Mockito.when(usuarioDAO.alterarSenha(emailValido, senhaValida, senhaNovaValida)).thenReturn(true);

        sucessoAlteracao = trocaSenha.alterarSenha(emailValido, senhaValida, senhaNovaValida);

        assertTrue(sucessoAlteracao);
    }

    @Test
    public void testeAlterarSenhaIncorreta(){
        boolean sucessoAlteracao;
        String senhaIncorreta = "espirito666";

        Mockito.when(trocaSenha.alterarSenha(emailValido, senhaIncorreta, senhaValida)).thenReturn(false);

        sucessoAlteracao = trocaSenha.alterarSenha(emailValido, senhaIncorreta, senhaValida);

        assertFalse(sucessoAlteracao);
    }

    @Test
    public void testeAlterarSenhaNovaVazia(){
        String senhaNovaVazia = "";

        assertThrows(CampoObrigatorioException.class,
                () -> trocaSenha.alterarSenha(emailValido, senhaValida, senhaNovaVazia));

        Mockito.verifyNoInteractions(usuarioDAO);
    }

    @Test
    public void testeAlterarSenhaNovaNula(){
        assertThrows(CampoObrigatorioException.class,
                () -> trocaSenha.alterarSenha(emailValido, senhaValida, null));

        Mockito.verifyNoInteractions(usuarioDAO);
    }

    @Test
    public void testeAlterarSenhaAtualVazia(){
        String senhaNovaVazia = "";
        String senhaNovaValida = "Theofficetop1!";

        assertThrows(CampoObrigatorioException.class,
                () -> trocaSenha.alterarSenha(emailValido, senhaNovaVazia, senhaNovaValida));

        Mockito.verifyNoInteractions(usuarioDAO);
    }

    @Test
    public void testeAlterarSenhaAtualNula(){
        String senhaNovaValida = "Theofficetop1!";

        assertThrows(CampoObrigatorioException.class,
                () -> trocaSenha.alterarSenha(emailValido, null, senhaNovaValida));

        Mockito.verifyNoInteractions(usuarioDAO);
    }

    @Test
    public void testeAlterarSenhaInvalida(){
        String senhaNovaInvalida = "senha";

        assertThrows(SenhaInvalidaException.class,
                () -> trocaSenha.alterarSenha(emailValido, senhaValida, senhaNovaInvalida));

        Mockito.verifyNoInteractions(usuarioDAO);
    }

    @Test
    public void testeAlterarSenhasIguais(){
        assertThrows(SenhaInvalidaException.class,
                () -> trocaSenha.alterarSenha(emailValido, senhaValida, senhaValida));
    }
}
