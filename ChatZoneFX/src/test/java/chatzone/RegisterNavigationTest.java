package chatzone;

import br.inatel.redesocial.autenticacao.Cadastro;
import br.inatel.redesocial.autenticacao.Login;
import br.inatel.redesocial.exceptions.EmailJaCadastradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class RegisterNavigationTest {

    private Navigator navigator;
    private Login login;
    private Cadastro cadastro;
    private AuthController controller;

    @BeforeEach
    void setup() {
        navigator = mock(Navigator.class);
        login = mock(Login.class);
        cadastro = mock(Cadastro.class);
        controller = new AuthController(login, cadastro, navigator);
    }

    @Test
    void registroValido_cadastraENavegaParaLobby() throws Exception {
        controller.register("user@email.com", "Senha@123","teste");
        verify(cadastro).criarUsuario("user@email.com", "Senha@123", "teste");
        verify(navigator).showLobby("user@email.com");
    }

    @Test
    void registroEmailInvalido_naoNavega() throws Exception {
        controller.register("invalido", "Senha@123","teste");
        verify(cadastro, never()).criarUsuario(any(), any(),any());
        verify(navigator, never()).showLobby(any());
    }

    @Test
    void registroSenhaInvalida_naoNavega() throws Exception {
        controller.register("user@email.com", "fraca","teste");
        verify(cadastro, never()).criarUsuario(any(), any(),any());
        verify(navigator, never()).showLobby(any());
    }

    @Test
    void registroEmailESenhaInvalidos_naoNavega() throws Exception {
        controller.register("invalido", "fraca","teste");
        verify(navigator, never()).showLobby(any());
    }

    @Test
    void registroComEmailJaCadastrado_naoNavega() throws Exception {
        doThrow(new EmailJaCadastradoException("E-mail já está em uso"))
                .when(cadastro).criarUsuario(any(), any(), any());

        controller.register("user@email.com", "Senha@123","teste");

        verify(navigator, never()).showLobby(any());
    }
}