package chatzone;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class AuthController {

    @FXML private Button tabLogin, tabRegister;
    @FXML private VBox   panelLogin, panelRegister;

    // Login
    @FXML private TextField     loginEmail;
    @FXML private PasswordField loginSenha;
    @FXML private Label         loginEmailErr;

    // Registro
    @FXML private TextField     regEmail;
    @FXML private PasswordField regSenha;
    @FXML private Label         regEmailErr, regSenhaErr;

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

    @FXML
    void onLogin() {
        String email = loginEmail.getText().trim();
        String senha = loginSenha.getText();

        if (!isValidEmail(email)) {
            loginEmailErr.setText("Email inválido");
            loginEmailErr.setVisible(true);
            return;
        }
        loginEmailErr.setVisible(false);

        // TODO: chamar API de login via HTTP
        // Por enquanto navega direto para o lobby
        try {
            Main.showLobby(email);
        } catch (Exception e) {
            showToast("Erro ao navegar: " + e.getMessage());
        }
    }

    @FXML
    void onRegister() {
        String email = regEmail.getText().trim();
        String senha = regSenha.getText();
        boolean ok = true;

        if (!isValidEmail(email)) {
            regEmailErr.setText("Email inválido");
            regEmailErr.setVisible(true);
            ok = false;
        } else {
            regEmailErr.setVisible(false);
        }

        if (!isValidSenha(senha)) {
            regSenhaErr.setText("Mín. 8 chars · 1 maiúscula · 1 número · 1 especial ($*&@#!)");
            regSenhaErr.setVisible(true);
            ok = false;
        } else {
            regSenhaErr.setVisible(false);
        }

        if (!ok) return;

        // TODO: chamar API de registro via HTTP
        try {
            Main.showLobby(email);
        } catch (Exception e) {
            showToast("Erro ao navegar: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+(\\.[a-z]+)?$");
    }

    private boolean isValidSenha(String senha) {
        return senha.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$*&@#!]).{8,}$");
    }

    private void showToast(String msg) {
        // TODO: implementar toast overlay
        System.out.println("[TOAST] " + msg);
    }
}
