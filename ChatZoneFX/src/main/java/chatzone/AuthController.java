package chatzone;

import br.inatel.redesocial.usuario.Cadastro;
import br.inatel.redesocial.usuario.Login;
import br.inatel.redesocial.usuario.Usuario;
import br.inatel.redesocial.banco.funcoes.CadastroDAO;
import br.inatel.redesocial.banco.funcoes.LoginDAO;
import br.inatel.redesocial.exceptions.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class AuthController {
    @FXML private Button tabLogin, tabRegister;
    @FXML private VBox panelLogin, panelRegister;
    @FXML private TextField loginEmail, loginSenhaTexto;
    @FXML private PasswordField loginSenha;
    @FXML private Label loginEmailErr, loginFeedback;
    @FXML private TextField regNome, regEmail, regSenhaTexto;
    @FXML private PasswordField regSenha;
    @FXML Label regNomeErr, regEmailErr, regSenhaErr;
    @FXML private Label regFeedback;

    private final Login login;
    private final Cadastro cadastro;
    private final Navigator navigator;

    public AuthController() {
        this(new Login(new LoginDAO()), new Cadastro(new CadastroDAO()), Main::showLobby);
    }

    public AuthController(Login login, Cadastro cadastro, Navigator navigator) {
        this.login = login;
        this.cadastro = cadastro;
        this.navigator = navigator;
    }

    @FXML void onTabLogin() {
        hideFeedback(regFeedback);
        tabLogin.getStyleClass().add("tab-active");
        tabRegister.getStyleClass().remove("tab-active");
        panelLogin.setVisible(true); panelLogin.setManaged(true);
        panelRegister.setVisible(false); panelRegister.setManaged(false);
    }

    @FXML void onTabRegister() {
        hideFeedback(loginFeedback);
        tabRegister.getStyleClass().add("tab-active");
        tabLogin.getStyleClass().remove("tab-active");
        panelRegister.setVisible(true); panelRegister.setManaged(true);
        panelLogin.setVisible(false); panelLogin.setManaged(false);
    }

    public boolean login(String email, String senha) {
        if (!Login.verificarEmail(email) || !Login.verificarSenha(senha)) return false;
        Usuario usuario = new Usuario(email, senha);
        try {
            login.autenticarUsuario(usuario, senha);
            Session.setUser(usuario.getEmail(), usuario.getNome());
        } catch (FalhaNoLoginException | CampoObrigatorioException e) {
            return false;
        }
        try { navigator.showLobby(email); }
        catch (Exception e) { showToast("Erro ao navegar: " + e.getMessage()); }
        return true;
    }

    public boolean register(String email, String senha, String nome) {
        try {
            cadastro.criarUsuario(email, senha, nome);
            Session.setUser(email, nome);
        } catch (EmailJaCadastradoException | EmailInvalidoException e) {
            showFieldError(regEmailErr, e.getMessage());
            showFeedback(regFeedback, "Nao foi possivel criar a conta. Revise o e-mail informado.");
            return false;
        } catch (NomeInvalidoException e) {
            showFieldError(regNomeErr, e.getMessage());
            showFeedback(regFeedback, "O nome de usuario nao atende aos criterios exigidos.");
            return false;
        } catch (SenhaInvalidaException e) {
            showFieldError(regSenhaErr, e.getMessage());
            showFeedback(regFeedback, "A senha nao atende aos criterios de seguranca.");
            return false;
        } catch (CampoObrigatorioException e) {
            showFeedback(regFeedback, "Preencha todos os campos para criar a conta.");
            return false;
        } catch (RuntimeException e) {
            showFeedback(regFeedback, "Nao foi possivel criar a conta agora. Tente novamente.");
            return false;
        }
        try { navigator.showLobby(email); }
        catch (Exception e) { showToast("Erro ao navegar: " + e.getMessage()); }
        return true;
    }

    @FXML void onLogin() {
        String email = loginEmail.getText().trim();
        String senha = senhaAtual(loginSenha, loginSenhaTexto);
        clearLoginErrors();
        if (email.isBlank()) { showLoginError("Informe seu e-mail para entrar."); return; }
        if (!Login.verificarEmail(email)) { showLoginError("Digite um e-mail valido, por exemplo: voce@email.com."); return; }
        if (senha.isBlank()) { showLoginError("Informe sua senha para entrar."); return; }
        if (!Login.verificarSenha(senha)) {
            showLoginError("A senha deve ter 8+ caracteres, maiuscula, minuscula, numero e simbolo.");
            return;
        }
        if (!login(email, senha)) showLoginError("E-mail ou senha incorretos. Confira os dados ou crie uma conta.");
    }

    @FXML void onRegister() {
        String email = regEmail.getText().trim();
        String senha = senhaAtual(regSenha, regSenhaTexto);
        String nome = regNome.getText().trim();
        boolean ok = true;
        clearRegisterErrors();
        if (!Login.verificarNome(nome)) {
            showFieldError(regNomeErr, "Use de 3 a 16 caracteres: letras, numeros ou _."); ok = false;
        }
        if (!Login.verificarEmail(email)) {
            showFieldError(regEmailErr, email.isBlank() ? "Informe seu e-mail." : "Digite um e-mail valido."); ok = false;
        }
        if (!Login.verificarSenha(senha)) {
            showFieldError(regSenhaErr, senha.isBlank() ? "Informe uma senha." : "Use 8+ caracteres, maiuscula, minuscula, numero e simbolo."); ok = false;
        }
        if (!ok) { showFeedback(regFeedback, "Revise os campos destacados antes de criar sua conta."); return; }
        register(email, senha, nome);
    }

    @FXML void onToggleLoginSenha() { alternarSenha(loginSenha, loginSenhaTexto); }
    @FXML void onToggleRegSenha() { alternarSenha(regSenha, regSenhaTexto); }

    private void clearLoginErrors() {
        hideFeedback(loginFeedback);
        if (loginEmailErr != null) loginEmailErr.setVisible(false);
    }

    private void clearRegisterErrors() {
        hideFeedback(regFeedback);
        if (regNomeErr != null) regNomeErr.setVisible(false);
        if (regEmailErr != null) regEmailErr.setVisible(false);
        if (regSenhaErr != null) regSenhaErr.setVisible(false);
    }

    private void showLoginError(String message) { showFeedback(loginFeedback, message); }

    private void showFieldError(Label label, String message) {
        if (label == null) return;
        label.setText(message == null ? "Nao foi possivel concluir a operacao." : message);
        label.setVisible(true); label.setManaged(true);
    }

    private void showFeedback(Label label, String message) {
        if (label == null) return;
        label.setText(message); label.setVisible(true); label.setManaged(true);
    }

    private void hideFeedback(Label label) {
        if (label != null) { label.setVisible(false); label.setManaged(false); }
    }

    private void alternarSenha(PasswordField senha, TextField texto) {
        boolean mostrar = !texto.isVisible();
        texto.setText(senha.getText());
        senha.setVisible(!mostrar); senha.setManaged(!mostrar);
        texto.setVisible(mostrar); texto.setManaged(mostrar);
    }

    private String senhaAtual(PasswordField senha, TextField texto) {
        return texto.isVisible() ? texto.getText() : senha.getText();
    }

    private void showToast(String msg) { System.out.println("[TOAST] " + msg); }
}
