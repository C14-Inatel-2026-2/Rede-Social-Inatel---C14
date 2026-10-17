package chatzone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class RegisterNavigationTest {

    private Navigator navigator;
    private AuthController controller;

    @BeforeEach
    void setup() {
        navigator = mock(Navigator.class);
        controller = new AuthController(navigator);
    }

    @Test
    void registroValido_navegaParaLobby() throws Exception {
        controller.register("user@email.com", "Senha@123");
        verify(navigator).showLobby("user@email.com");
    }

    @Test
    void registroEmailInvalido_naoNavega() throws Exception {
        controller.register("invalido", "Senha@123");
        verify(navigator, never()).showLobby(any());
    }

    @Test
    void registroSenhaInvalida_naoNavega() throws Exception {
        controller.register("user@email.com", "fraca");
        verify(navigator, never()).showLobby(any());
    }

    @Test
    void registroEmailESenhaInvalidos_naoNavega() throws Exception {
        controller.register("invalido", "fraca");
        verify(navigator, never()).showLobby(any());
    }
}
