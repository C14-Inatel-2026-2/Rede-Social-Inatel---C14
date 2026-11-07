package chatzone;

import br.inatel.redesocial.autenticacao.Cadastro;
import br.inatel.redesocial.autenticacao.Login;
import br.inatel.redesocial.exceptions.FalhaNoLoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginNavigationTest {

    private Navigator navigator;
    private Login login;
    private Cadastro cadastro;
    private AuthController controller;

    @BeforeEach
    void setup() {
        navigator = mock(Navigator.class);
        login = mock(Login.class);
        cadastro = mock(Cadastro.class);
        when(login.autenticarUsuario(any(), any())).thenReturn(true);
        controller = new AuthController(login, cadastro, navigator);
    }

    @Test
    void loginValido_navegaParaLobby() throws Exception {
        controller.login("user@email.com", "Senha@123");
        verify(navigator).showLobby("user@email.com");
    }

    @Test
    void loginEmailInvalido_naoNavega() throws Exception {
        controller.login("invalido", "Senha@123");
        verify(navigator, never()).showLobby(any());
        verify(login, never()).autenticarUsuario(any(), any());
    }

    @Test
    void loginSenhaInvalida_naoNavega() throws Exception {
        controller.login("user@email.com", "fraca");
        verify(navigator, never()).showLobby(any());
        verify(login, never()).autenticarUsuario(any(), any());
    }

    @Test
    void loginRecusadoPeloBanco_naoNavega() throws Exception {
        when(login.autenticarUsuario(any(), any()))
                .thenThrow(new FalhaNoLoginException("Credenciais incorretas"));

        assertFalse(controller.login("user@email.com", "Senha@123"));
        verify(navigator, never()).showLobby(any());
    }
}