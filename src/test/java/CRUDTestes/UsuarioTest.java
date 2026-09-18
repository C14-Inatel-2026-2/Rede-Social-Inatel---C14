package CRUDTestes;

import br.inatel.redesocial.autenticacao.Usuario;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {
    private Usuario usuario;
    private final String senha = "Chizinho67!";

    @BeforeEach
    public void setup(){
        String email = "christopher@inatel.br";
        this.usuario = new Usuario(email, senha);
    }

    @Test
    public void testeAlterarSenha(){
        String senhaNovaValida = "Theofficetop1!";
        boolean troca = usuario.alterarSenha(senha, senhaNovaValida);

        assertTrue(troca);
    }

    @Test
    public void testeAlterarSenhaIgual(){
        boolean troca = usuario.alterarSenha(senha, senha);

        assertFalse(troca);
    }

    @Test
    public void testeAlterarSenhaInvalida(){
        String senhaInvalida = "321";
        boolean troca = usuario.alterarSenha(senha, senhaInvalida);

        assertFalse(troca);
    }
}
