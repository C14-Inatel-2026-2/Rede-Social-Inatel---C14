package chatzone;

import br.inatel.redesocial.autenticacao.Cadastro;
import br.inatel.redesocial.autenticacao.Login;
import br.inatel.redesocial.exceptions.EmailJaCadastradoException;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import javafx.scene.control.*;
import br.inatel.redesocial.exceptions.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
    private Label regEmailErr, regNomeErr, regSenhaErr;

    @BeforeEach
    void setup() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException jaIniciado) {
            // toolkit já estava rodando
        }

        navigator = mock(Navigator.class);
        login = mock(Login.class);
        cadastro = mock(Cadastro.class);
        controller = new AuthController(login, cadastro, navigator);


        regEmailErr = mock(Label.class);
        regNomeErr = mock(Label.class);
        regSenhaErr = mock(Label.class);
        controller.regEmailErr = regEmailErr;
        controller.regNomeErr = regNomeErr;
        controller.regSenhaErr = regSenhaErr;
    }

    @Test
    void registroValido_cadastraENavegaParaLobby() throws Exception {
        controller.register("user@email.com", "Senha@123","teste");
        verify(cadastro).criarUsuario("user@email.com", "Senha@123", "teste");
        verify(navigator).showLobby("user@email.com");
    }

    @Test
    void registroEmailInvalido_naoNavega() throws Exception {
        doThrow(new EmailInvalidoException("Email inválido"))
                .when(cadastro).criarUsuario(any(), any(), any());

        boolean resultado = controller.register("invalido", "Senha@123", "teste");

        assertFalse(resultado);
        verify(regEmailErr).setText("Email inválido");
        verify(navigator, never()).showLobby(any());
    }

    @Test
    void registroSenhaInvalida_naoNavega() throws Exception {
        doThrow(new SenhaInvalidaException("Senha inválida"))
                .when(cadastro).criarUsuario(any(), any(), any());

        boolean resultado = controller.register("user@email.com", "fraca", "teste");

        assertFalse(resultado);
        verify(regSenhaErr).setText("Senha inválida");
        verify(navigator, never()).showLobby(any());
    }

    @Test
    void registroEmailESenhaInvalidos_naoNavega() throws Exception {
        doThrow(new EmailInvalidoException("Email inválido"))
                .when(cadastro).criarUsuario(any(), any(), any());

        boolean resultado = controller.register("invalido", "fraca", "teste");

        assertFalse(resultado);
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