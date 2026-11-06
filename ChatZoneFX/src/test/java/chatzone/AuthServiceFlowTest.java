package chatzone;

import br.inatel.redesocial.autenticacao.Cadastro;
import br.inatel.redesocial.autenticacao.Login;
import br.inatel.redesocial.autenticacao.Usuario;
import br.inatel.redesocial.exceptions.FalhaNoLoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class AuthServiceFlowTest {

    private static final String EMAIL = "user@email.com";
    private static final String SENHA = "Senha@123";
    private static final String NOME  = "user_01";

    private Navigator navigator;
    private AuthController controller;
    private Login login;
    private Cadastro cadastro;

    @BeforeEach
    void setup() {
        navigator = mock(Navigator.class);
        login = mock(Login.class);
        when(login.autenticarUsuario(any(), any())).thenReturn(true);
        cadastro = mock(Cadastro.class);
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

        assertDoesNotThrow(() -> controller.register("user@email.com", "Senha@123", "teste"));
        verify(navigator).showLobby("user@email.com");
    }
    @Test
    void cadastroValido_chamaCadastroComNomeENavegaParaLobby() throws Exception {
        boolean ok = controller.register(EMAIL, SENHA, NOME);

        assertTrue(ok);
        verify(cadastro).criarUsuario(EMAIL, SENHA, NOME, login);
        verify(navigator).showLobby(EMAIL);
    }

    @Test
    void loginValido_consultaAutenticacaoSemUsarONome() throws Exception {
        boolean ok = controller.login(EMAIL, SENHA);

        assertTrue(ok);
        verify(login).autenticarUsuario(any(Usuario.class), eq(SENHA));
        verify(navigator).showLobby(EMAIL);
    }

    @Test
    void loginComCredenciaisIncorretas_retornaFalseENaoNavega() throws Exception {
        doThrow(new FalhaNoLoginException("Falha no login."))
                .when(login).autenticarUsuario(any(), any());

        assertFalse(controller.login(EMAIL, "Errada@123"));
        verify(navigator, never()).showLobby(any());
    }

    @Test
    void loginRecusadoPeloBanco_naoNavega() throws Exception {
        doThrow(new FalhaNoLoginException("Falha no login."))
                .when(login).autenticarUsuario(any(), any());

        assertFalse(controller.login("user@email.com", "Senha@123"));
        verify(navigator, never()).showLobby(any());
    }
}