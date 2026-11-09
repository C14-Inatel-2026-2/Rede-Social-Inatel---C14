package CRUDTestes;

import br.inatel.redesocial.banco.funcoes.UsuarioDAO;
import br.inatel.redesocial.usuario.TrocarSenha;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class TrocarSenhaTest {
    TrocarSenha trocaSenha;

    private final String senha = "Chizinho67!";
    private final String email = "christopher@inatel.br";

    @Mock
    UsuarioDAO usuarioDAO;

    @BeforeEach
    public void setup(){
        this.trocaSenha = new TrocarSenha(usuarioDAO);
    }

    @Test
    public void testeAlterarSenha(){
        String senhaNovaValida = "Theofficetop1!";
        boolean senhaalterada;

        Mockito.when(usuarioDAO.alterarSenha(email, senha, senhaNovaValida)).thenReturn(true);

        senhaalterada = trocaSenha.alterarSenha(email, senha, senhaNovaValida);

        assertTrue(senhaalterada);
    }
}
