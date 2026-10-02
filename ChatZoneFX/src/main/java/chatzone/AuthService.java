package chatzone;

public class AuthService {

    public boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+(\\.[a-z]+)?$");
    }

    public boolean isValidSenha(String senha) {
        if (senha == null) return false;
        return senha.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$*&@#!]).{8,}$");
    }

    public String login(String email, String senha) {
        if (!isValidEmail(email)) throw new IllegalArgumentException("Email inválido");
        if (!isValidSenha(senha)) throw new IllegalArgumentException("Senha inválida");
        return "LOGIN_OK";
    }

    public String register(String email, String senha) {
        if (!isValidEmail(email)) throw new IllegalArgumentException("Email inválido");
        if (!isValidSenha(senha)) throw new IllegalArgumentException("Senha inválida");
        return "REGISTER_OK";
    }
}
