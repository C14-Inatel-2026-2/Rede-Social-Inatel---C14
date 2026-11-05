package br.inatel.redesocial.autenticacao;

public class Usuario {
    private final String email;
    private String senhaUsuario;
    private boolean logado;
    private String nome;



    public Usuario(String email, String senha) {
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
