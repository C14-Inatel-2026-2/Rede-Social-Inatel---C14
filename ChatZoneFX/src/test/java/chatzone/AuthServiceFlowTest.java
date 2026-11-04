package chatzone;

import br.inatel.redesocial.autenticacao.Cadastro;
import br.inatel.redesocial.autenticacao.Login;
import br.inatel.redesocial.autenticacao.Cadastro;
import br.inatel.redesocial.banco.funcoes.CadastroDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceFlowTest {

    private Navigator navigator;
    private AuthController controller;

    @BeforeEach
    void setup() {
        navigator = mock(Navigator.class);
        Login login = mock(Login.class);
        when(login.autenticarUsuario(any(), any())).thenReturn(true);
        Cadastro cadastro = mock(Cadastro.class);
        controller = new AuthController(login, cadastro, navigator);
    }

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
        doThrow(new Exception("Falha na navegação"))
                .when(navigator).showLobby("user@email.com");

        assertDoesNotThrow(() -> controller.login("user@email.com", "Senha@123"));
        verify(navigator).showLobby("user@email.com");
    }

    @Test
    void cadastroQuandoNavegacaoFalha_naoPropagaExcecao() throws Exception {
        doThrow(new Exception("Falha na navegação"))
                .when(navigator).showLobby("user@email.com");

        assertDoesNotThrow(() -> controller.register("user@email.com", "Senha@123"));
        verify(navigator).showLobby("user@email.com");
    }
}