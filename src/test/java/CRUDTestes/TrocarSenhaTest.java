package CRUDTestes;

import br.inatel.redesocial.banco.funcoes.UsuarioDAO;
import br.inatel.redesocial.exceptions.CampoObrigatorioException;
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

    private final String senhaNovaVazia = "";

    @Mock
    UsuarioDAO usuarioDAO;

    @BeforeEach
    public void setup(){
        this.trocaSenha = new TrocarSenha(usuarioDAO);
    }

    @Test
    public void testeAlterarSenha(){
        boolean senhaAlterada;

        Mockito.when(usuarioDAO.alterarSenha(emailValido, senhaValida, senhaNovaValida)).thenReturn(true);

        senhaAlterada = trocaSenha.alterarSenha(emailValido, senhaValida, senhaNovaValida);

        assertTrue(senhaAlterada);
    }

    @Test
    public void testeAlterarSenhaIgual(){
        assertThrows(CampoObrigatorioException.class,
                () -> trocaSenha.alterarSenha(emailValido, senhaValida, senhaNovaVazia));
    }
}
