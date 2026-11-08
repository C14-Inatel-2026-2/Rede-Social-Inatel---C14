package chatzone;

import br.inatel.redesocial.autenticacao.Cadastro;
import br.inatel.redesocial.autenticacao.Login;
import br.inatel.redesocial.autenticacao.Usuario;
import br.inatel.redesocial.banco.funcoes.CadastroDAO;
import br.inatel.redesocial.banco.funcoes.LoginDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import br.inatel.redesocial.exceptions.*;
import javafx.scene.layout.VBox;

public class AuthController {

    @FXML private Button tabLogin, tabRegister;
    @FXML private VBox   panelLogin, panelRegister;

    @FXML private TextField     loginEmail;
    @FXML private PasswordField loginSenha;
    @FXML private TextField loginSenhaTexto;
    @FXML private Label         loginEmailErr;

    @FXML private TextField     regNome;
    @FXML private TextField     regEmail;
    @FXML private PasswordField regSenha;
    @FXML private TextField regSenhaTexto;
    @FXML Label         regNomeErr;
    @FXML Label         regEmailErr, regSenhaErr;

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

    @FXML
    void onTabLogin() {
        tabLogin.getStyleClass().add("tab-active");
        tabRegister.getStyleClass().remove("tab-active");
        panelLogin.setVisible(true);
        panelLogin.setManaged(true);
        panelRegister.setVisible(false);
        panelRegister.setManaged(false);
    }

    @FXML
    void onTabRegister() {
        tabRegister.getStyleClass().add("tab-active");
        tabLogin.getStyleClass().remove("tab-active");
        panelRegister.setVisible(true);
        panelRegister.setManaged(true);
        panelLogin.setVisible(false);
        panelLogin.setManaged(false);
    }

    public boolean login(String email, String senha) {
        if (!Login.verificarEmail(email)
                || !Login.verificarSenha(senha)) return false;

        Usuario usuario = new Usuario(email, senha);

        try {
            login.autenticarUsuario(usuario, senha);
            Session.setUser(usuario.getEmail(), usuario.getNome());
        } catch (FalhaNoLoginException | CampoObrigatorioException e) {
            return false;
        }

        try {
            navigator.showLobby(email);
        } catch (Exception e) {
            showToast("Erro ao navegar: " + e.getMessage());
        }
        return true;
    }

    public boolean register(String email, String senha, String nome) {

        try {
            Usuario usuario = cadastro.criarUsuario(email, senha, nome);
            Session.setUser(usuario.getEmail(), usuario.getNome());
        } catch (EmailJaCadastradoException | EmailInvalidoException e) {
            regEmailErr.setText(e.getMessage());
            regEmailErr.setVisible(true);
            return false;
        } catch (NomeInvalidoException e) {
            regNomeErr.setText(e.getMessage());
            regNomeErr.setVisible(true);
            return false;
        } catch (SenhaInvalidaException e) {
            regSenhaErr.setText(e.getMessage());
            regSenhaErr.setVisible(true);
            return false;
        } catch (RuntimeException e) {
            regEmailErr.setText(e.getMessage());
            regEmailErr.setVisible(true);
            return false;
        }

        try {
            navigator.showLobby(email);
        } catch (RuntimeException e) {
            showToast(e.getMessage());
        } catch (Exception e) {
            showToast("Erro ao navegar: " + e.getMessage());
        }
        return true;
    }

    @FXML
    void onLogin() {
        String email = loginEmail.getText().trim();
        String senha = senhaAtual(loginSenha, loginSenhaTexto);

        if (!Login.verificarEmail(email)) {
            loginEmailErr.setText("Email inválido");
            loginEmailErr.setVisible(true);
            return;
        }
        loginEmailErr.setVisible(false);

        if (!login(email, senha)) {
            loginEmailErr.setText("Email ou senha incorretos");
            loginEmailErr.setVisible(true);
        }
    }

    @FXML
    void onRegister() {
        String email = regEmail.getText().trim();
        String senha = senhaAtual(regSenha, regSenhaTexto);
        String nome = regNome.getText().trim();
        boolean ok = true;

        if (!Login.verificarNome(nome)) {
            regEmailErr.setText("Nome: 3 a 16 caracteres (letras, números ou _)");
            ok = false;
        } else {
            regNomeErr.setVisible(false);
        }

        if (!Login.verificarEmail(email)) {
            regEmailErr.setText("Email inválido");
            regEmailErr.setVisible(true);
            ok = false;
        } else {
            regEmailErr.setVisible(false);
        }

        if (!Login.verificarSenha(senha)) {
            regSenhaErr.setText("Mín. 8 chars · 1 maiúscula · 1 número · 1 especial ($*&@#!)");
            regSenhaErr.setVisible(true);
            ok = false;
        } else {
            regSenhaErr.setVisible(false);
        }

        if (!ok) return;
        register(email, senha,nome);
    }

    @FXML
    void onToggleLoginSenha() {
        alternarSenha(loginSenha, loginSenhaTexto);
    }

    @FXML
    void onToggleRegSenha() {
        alternarSenha(regSenha, regSenhaTexto);
    }

    private void alternarSenha(PasswordField senha, TextField texto) {
        boolean mostrar = !texto.isVisible();
        texto.setText(senha.getText());
        senha.setVisible(!mostrar);
        senha.setManaged(!mostrar);
        texto.setVisible(mostrar);
        texto.setManaged(mostrar);
    }

    private String senhaAtual(PasswordField senha, TextField texto) {
        return texto.isVisible() ? texto.getText() : senha.getText();
    }

    private void showToast(String msg) {
        System.out.println("[TOAST] " + msg);
    }
}
