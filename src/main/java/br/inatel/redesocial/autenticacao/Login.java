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

    public static String verificarEmail(String emailVerificado){
        if (emailVerificado.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+(\\.[a-z]+)?$")){
            return emailVerificado;
        }

        return "Email inválido";
    }

    public static String verificarSenha(String senhaVerificada){
        if (senhaVerificada.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$*&@#!]).{8,}$")){
            return senhaVerificada;
        }

        return "Senha inválida";
    }
}
