package CRUDTestes;

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
    private final String senhaNovaValida = "Theofficetop1!";
    private final String senhaNovaInvalida = "senha";

    private final String senhaNovaVazia = "";

    @Mock
    UsuarioDAO usuarioDAO;

    @BeforeEach
    public void setup(){
        this.trocaSenha = new TrocarSenha(usuarioDAO);
    }

    @Test
    public void testeAlterarSenha(){
        boolean sucessoAlteracao;

        Mockito.when(usuarioDAO.alterarSenha(emailValido, senhaValida, senhaNovaValida)).thenReturn(true);

        sucessoAlteracao = trocaSenha.alterarSenha(emailValido, senhaValida, senhaNovaValida);

        assertTrue(sucessoAlteracao);
    }

    @Test
    public void testeAlterarSenhaNula(){
        assertThrows(CampoObrigatorioException.class,
                () -> trocaSenha.alterarSenha(emailValido, senhaValida, senhaNovaVazia));
    }

    @Test
    public void testeAlterarSenhaInvalida(){
        assertThrows(SenhaInvalidaException.class,
                () -> trocaSenha.alterarSenha(emailValido, senhaValida, senhaNovaInvalida));
    }

    @Test
    public void testeAlterarSenhasIguais(){
        assertThrows(SenhaInvalidaException.class,
                () -> trocaSenha.alterarSenha(emailValido, senhaValida, senhaValida));
    }
}
