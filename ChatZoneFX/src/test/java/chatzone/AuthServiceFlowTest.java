package chatzone;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthServiceFlowTest {

    @Test
    void loginComCredenciaisValidas_retornaLoginOk() {
        AuthService authService = new AuthService();

        assertEquals("LOGIN_OK", authService.login("user@email.com", "Senha@123"));
    }

    @Test
    void cadastroComSenhaInvalida_lancaExcecao() {
        AuthService authService = new AuthService();

        assertThrows(IllegalArgumentException.class,
                () -> authService.register("user@email.com", "fraca"));
    }

    @Test
    void loginQuandoNavegacaoFalha_naoPropagaExcecao() throws Exception {
        Navigator navigator = mock(Navigator.class);
        doThrow(new Exception("Falha na navegação"))
                .when(navigator).showLobby("user@email.com");
        AuthController controller = new AuthController(navigator);

        assertDoesNotThrow(() -> controller.login("user@email.com", "Senha@123"));
        verify(navigator).showLobby("user@email.com");
    }

    @Test
    void cadastroQuandoNavegacaoFalha_naoPropagaExcecao() throws Exception {
        Navigator navigator = mock(Navigator.class);
        doThrow(new Exception("Falha na navegação"))
                .when(navigator).showLobby("user@email.com");
        AuthController controller = new AuthController(navigator);

        assertDoesNotThrow(() -> controller.register("user@email.com", "Senha@123"));
        verify(navigator).showLobby("user@email.com");
    }
}