package br.inatel.redesocial.autenticacao;

import br.inatel.redesocial.exceptions.CampoObrigatorioException;
import br.inatel.redesocial.exceptions.EmailInvalidoException;
import br.inatel.redesocial.exceptions.SenhaInvalidaException;

public class Usuario {
    private final String email;
    private String senhaUsuario;
    private boolean logado;

    public Usuario(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new CampoObrigatorioException("E-mail e senha são obrigatórios");
        }

        if(!Login.verificarEmail(email)){
            throw new EmailInvalidoException("Formato de email inválido");
        }

        if(!Login.verificarSenha(senha)) {
            throw new SenhaInvalidaException("Formato de senha inválida");
        }

        this.email = email;
        this.senhaUsuario = senha;
        this.logado = false;
    }

    public boolean alterarSenha(String senhaAtual, String senhaNova){
        if (!senhaAtual.equals(senhaUsuario)){
            return false;
        }

        if (senhaNova.equals(senhaUsuario)){
            return false;
        }

        if (!Login.verificarSenha(senhaNova)){
            return false;
        }

        this.senhaUsuario = senhaNova;
        return true;
    }

    public void setLogado(boolean logado) {
        this.logado = logado;
    }

    public String getEmail() {
        return email;
    }

    public boolean getLogado() {
        return logado;
    }
}
