package br.inatel.redesocial.autenticacao;

import br.inatel.redesocial.exceptions.CampoObrigatorioException;
import br.inatel.redesocial.exceptions.EmailInvalidoException;
import br.inatel.redesocial.exceptions.SenhaInvalidaException;

public class Usuario {
    private final String email;
    private String senhaUsuario;
    private boolean logado;
    private String nome;

    public Usuario(String email, String senha, String nome) {

        verificacaoCampoObrigatorio(email, senha, nome);
        verificacaoEmailESenha(email, senha);

        this.email = email;
        this.senhaUsuario = senha;
        this.logado = false;
        this.nome = nome;
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

    private void verificacaoCampoObrigatorio(String email, String senha, String nome){
        if (email == null || email.isBlank() || senha == null || senha.isBlank()
        || nome == null || nome.isBlank()) {
            throw new CampoObrigatorioException("E-mail, senha e nome são obrigatórios");
        }
    }

    private void verificacaoEmailESenha(String email, String senha){
        if(!Login.verificarEmail(email)){
            throw new EmailInvalidoException("Formato de email inválido");
        }

        if(!Login.verificarSenha(senha)) {
            throw new SenhaInvalidaException("Formato de senha inválida");
        }
    }

    public String getNome() {
        return nome;
    }
}
