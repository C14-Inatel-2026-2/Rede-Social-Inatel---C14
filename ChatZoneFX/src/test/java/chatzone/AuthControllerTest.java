package chatzone;

import br.inatel.redesocial.exceptions.EmailInvalidoException;
import br.inatel.redesocial.exceptions.EmailJaCadastradoException;
import br.inatel.redesocial.exceptions.FalhaNoLoginException;
import br.inatel.redesocial.exceptions.SenhaInvalidaException;
import br.inatel.redesocial.usuario.Cadastro;
import br.inatel.redesocial.usuario.Login;
import br.inatel.redesocial.usuario.Usuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(JavaFxExtension.class)
public class AuthControllerTest {

    private static final String EMAIL = "user@email.com";
    private static final String EMAIL_INVALIDO = "invalido";
    private static final String SENHA = "Senha@123";
    private static final String SENHA_ERRADA = "Errada@123";
    private static final String SENHA_FRACA = "fraca";
    private static final String NOME = "user_01";

    private Button tabRegister;
    private TextField loginEmail;
    private VBox panelLogin;
    private VBox panelRegister;
    private Navigator navigator;
    private Login login;
    private Cadastro cadastro;
    private AuthController controller;
    private Label regEmailErr;
    private PasswordField loginSenha;
    private TextField loginSenhaTexto;
    private Label regSenhaErr;
    private Label loginFeedback;

    @BeforeEach
    public void setup() throws Exception {
        navigator = mock(Navigator.class);
        login = mock(Login.class);
        cadastro = mock(Cadastro.class);
        when(login.autenticarUsuario(any(Usuario.class), eq(SENHA))).thenReturn(true);
        controller = new AuthController(login, cadastro, navigator);

        FXMLLoader loader = new FXMLLoader(AuthController.class.getResource("auth.fxml"));
        loader.setControllerFactory(tipo -> controller);
        loader.load();

        tabRegister = (Button) loader.getNamespace().get("tabRegister");
        panelLogin = (VBox) loader.getNamespace().get("panelLogin");
        loginSenha = (PasswordField) loader.getNamespace().get("loginSenha");
        loginSenhaTexto = (TextField) loader.getNamespace().get("loginSenhaTexto");
        loginEmail = (TextField) loader.getNamespace().get("loginEmail");
        loginFeedback = (Label) loader.getNamespace().get("loginFeedback");
        panelRegister = (VBox) loader.getNamespace().get("panelRegister");
        regEmailErr = mock(Label.class);
        regSenhaErr = mock(Label.class);
        controller.regEmailErr = regEmailErr;
        controller.regSenhaErr = regSenhaErr;
        controller.regNomeErr = mock(Label.class);
    }

    @Test
    public void testeLoginValidoRetornaTrue() {
        boolean resultado = controller.login(EMAIL, SENHA);
        assertTrue(resultado);
    }

    @Test
    public void testeLoginValidoAutenticaComASenhaDigitada() {
        controller.login(EMAIL, SENHA);
        verify(login).autenticarUsuario(any(Usuario.class), eq(SENHA));
    }

    @Test
    public void testeLoginValidoNavegaParaLobby() throws Exception {
        controller.login(EMAIL, SENHA);
        verify(navigator).showLobby(EMAIL);
    }

    @Test
    public void testeLoginComEmailInvalidoNaoAutentica() {
        controller.login(EMAIL_INVALIDO, SENHA);
        verify(login, never()).autenticarUsuario(any(), any());
    }

    @Test
    public void testeLoginComEmailInvalidoNaoNavega() throws Exception {
        controller.login(EMAIL_INVALIDO, SENHA);
        verify(navigator, never()).showLobby(any());
    }

    @Test
    public void testeLoginComSenhaInvalidaNaoAutentica() {
        controller.login(EMAIL, SENHA_FRACA);
        verify(login, never()).autenticarUsuario(any(), any());
    }

    @Test
    public void testeLoginComSenhaInvalidaNaoNavega() throws Exception {
        controller.login(EMAIL, SENHA_FRACA);
        verify(navigator, never()).showLobby(any());
    }

    @Test
    public void testeLoginRecusadoRetornaFalse() {
        doThrow(new FalhaNoLoginException("Falha no login."))
                .when(login).autenticarUsuario(any(Usuario.class), eq(SENHA_ERRADA));

        boolean resultado = controller.login(EMAIL, SENHA_ERRADA);
        assertFalse(resultado);
    }

    @Test
    public void testeLoginRecusadoNaoNavega() throws Exception {
        doThrow(new FalhaNoLoginException("Falha no login."))
                .when(login).autenticarUsuario(any(Usuario.class), eq(SENHA_ERRADA));

        controller.login(EMAIL, SENHA_ERRADA);
        verify(navigator, never()).showLobby(any());
    }

    @Test
    public void testeLoginComFalhaNaNavegacaoNaoLancaExcecao() throws Exception {
        doThrow(new Exception("Falha na navegação")).when(navigator).showLobby(EMAIL);
        assertDoesNotThrow(() -> controller.login(EMAIL, SENHA));
    }

    @Test
    public void testeCadastroValidoCriaUsuarioComNome() {
        controller.register(EMAIL, SENHA, NOME);
        verify(cadastro).criarUsuario(EMAIL, SENHA, NOME);
    }

    @Test
    public void testeCadastroValidoNavegaParaLobby() throws Exception {
        controller.register(EMAIL, SENHA, NOME);
        verify(navigator).showLobby(EMAIL);
    }

    @Test
    public void testeCadastroComEmailInvalidoRetornaFalse() {
        doThrow(new EmailInvalidoException("Email inválido"))
                .when(cadastro).criarUsuario(EMAIL_INVALIDO, SENHA, NOME);

        boolean resultado = controller.register(EMAIL_INVALIDO, SENHA, NOME);
        assertFalse(resultado);
    }

    @Test
    public void testeCadastroComEmailInvalidoMostraErroNoEmail() {
        doThrow(new EmailInvalidoException("Email inválido"))
                .when(cadastro).criarUsuario(EMAIL_INVALIDO, SENHA, NOME);

        controller.register(EMAIL_INVALIDO, SENHA, NOME);
        verify(regEmailErr).setText("Email inválido");
    }

    @Test
    public void testeCadastroComSenhaInvalidaMostraErroNaSenha() {
        doThrow(new SenhaInvalidaException("Senha inválida"))
                .when(cadastro).criarUsuario(EMAIL, SENHA_FRACA, NOME);

        controller.register(EMAIL, SENHA_FRACA, NOME);
        verify(regSenhaErr).setText("Senha inválida");
    }

    @Test
    public void testeCadastroComSenhaInvalidaNaoNavega() throws Exception {
        doThrow(new SenhaInvalidaException("Senha inválida"))
                .when(cadastro).criarUsuario(EMAIL, SENHA_FRACA, NOME);

        controller.register(EMAIL, SENHA_FRACA, NOME);
        verify(navigator, never()).showLobby(any());
    }

    @Test
    public void testeCadastroComEmailJaCadastradoNaoNavega() throws Exception {
        doThrow(new EmailJaCadastradoException("E-mail já está em uso"))
                .when(cadastro).criarUsuario(EMAIL, SENHA, NOME);

        controller.register(EMAIL, SENHA, NOME);
        verify(navigator, never()).showLobby(any());
    }

    @Test
    public void testeCadastroComFalhaNaNavegacaoNaoLancaExcecao() throws Exception {
        doThrow(new Exception("Falha na navegação")).when(navigator).showLobby(EMAIL);
        assertDoesNotThrow(() -> controller.register(EMAIL, SENHA, NOME));
    }

    @Test
    public void testeMostrarSenhaDoLoginCopiaOTexto() {
        loginSenha.setText(SENHA);

        controller.onToggleLoginSenha();

        assertEquals(SENHA, loginSenhaTexto.getText());
    }

    @Test
    public void testeMostrarSenhaDoLoginEscondeOCampoDeSenha() {
        controller.onToggleLoginSenha();

        assertFalse(loginSenha.isVisible());
    }
    @Test
    public void testeAbaCadastroMostraPainelDeCadastro() {
        controller.onTabRegister();
        assertTrue(panelRegister.isVisible());
    }
    @Test
    public void testeAbaCadastroEscondePainelDeLogin() {
        controller.onTabRegister();
        assertFalse(panelLogin.isVisible());
    }

    @Test
    public void testeAbaCadastroFicaDestacada() {
        controller.onTabRegister();
        assertTrue(tabRegister.getStyleClass().contains("tab-active"));
    }
    @Test
    public void testeAbaLoginVoltaParaOLogin() {
        controller.onTabRegister();
        controller.onTabLogin();

        assertTrue(panelLogin.isVisible());
    }
    @Test

    public void testeBotaoEntrarSemEmailMostraAviso() {
        loginEmail.setText("");
        loginSenha.setText(SENHA);

        controller.onLogin();

        assertEquals("Informe seu e-mail para entrar.", loginFeedback.getText());
    }

    @Test
    public void testeBotaoEntrarComEmailInvalidoMostraAviso() {
        loginEmail.setText(EMAIL_INVALIDO);
        loginSenha.setText(SENHA);

        controller.onLogin();

        assertEquals("Digite um e-mail valido, por exemplo: voce@email.com.", loginFeedback.getText());
    }

    @Test
    public void testeBotaoEntrarSemSenhaMostraAviso() {
        loginEmail.setText(EMAIL);
        loginSenha.setText("");

        controller.onLogin();

        assertEquals("Informe sua senha para entrar.", loginFeedback.getText());
    }

    @Test
    public void testeBotaoEntrarComSenhaFracaMostraAviso() {
        loginEmail.setText(EMAIL);
        loginSenha.setText(SENHA_FRACA);

        controller.onLogin();

        assertEquals("A senha deve ter 8+ caracteres, maiuscula, minuscula, numero e simbolo.", loginFeedback.getText());
    }
}