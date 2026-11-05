package br.inatel.redesocial.autenticacao;

import br.inatel.redesocial.banco.funcoes.LoginDAO;
import br.inatel.redesocial.exceptions.CampoObrigatorioException;
import br.inatel.redesocial.exceptions.FalhaNoLoginException;

public class Login{
    LoginDAO loginDAO;

    public Login(LoginDAO loginDAO) {
        this.loginDAO = loginDAO;
    }

    public boolean autenticarUsuario(Usuario usuario, String senhaDigitada) {
        verificacaoCampoObrigatorio(usuario.getEmail(), senhaDigitada);

        String nome = loginDAO.auteticaUser(usuario.getEmail(), senhaDigitada);

        verificaSucessoNoLogin(nome);

        usuario.setNome(nome);
        usuario.setLogado(true);

        return true;
    }

    public static boolean verificarEmail(String email) {
        return email.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+(\\.[a-z]+)?$");
    }

    public static boolean verificarSenha(String senha) {
        return senha.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$*&@#!]).{8,}$");
    }

    public static boolean verificarNome(String nome) {
        return nome.matches("^[A-Za-z0-9_]{3,16}$");
    }

    private void verificaSucessoNoLogin(String nome){
        if(nome == null) {
            throw new FalhaNoLoginException("Falha no login.");
        }
    }

    private void verificacaoCampoObrigatorio(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank())
            throw new CampoObrigatorioException("E-mail e senha são obrigatórios");
    }
}