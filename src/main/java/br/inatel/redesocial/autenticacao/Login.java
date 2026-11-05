package br.inatel.redesocial.autenticacao;

import br.inatel.redesocial.banco.funcoes.LoginDAO;

public class Login {
    LoginDAO loginDAO;

    public Login(LoginDAO loginDAO){
        this.loginDAO = loginDAO;
    }

    public boolean autenticarUsuario(Usuario usuario, String senhaDigitada) {
        boolean autenticado = loginDAO.auteticaUser(usuario.getEmail(), senhaDigitada);

        usuario.setLogado(autenticado);

        return autenticado;
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
