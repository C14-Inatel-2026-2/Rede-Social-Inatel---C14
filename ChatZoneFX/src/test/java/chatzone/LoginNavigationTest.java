package chatzone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class LoginNavigationTest {

    private Navigator navigator;
    private AuthController controller;

    @BeforeEach
    void setup() {
        navigator = mock(Navigator.class);
        controller = new AuthController(navigator);
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
    }

    @Test
    void loginSenhaInvalida_naoNavega() throws Exception {
        controller.login("user@email.com", "fraca");
        verify(navigator, never()).showLobby(any());
    }
}
