package br.inatel.redesocial.autenticacao;

public class Usuario {
    private final String email;
    private String senhaUsuario;
    private boolean logado;

    public Usuario(String email, String senha){
        this.email = Login.verificarEmail(email);
        this.senhaUsuario = Login.verificarSenha(senha);
    }

    public boolean alterarSenha(String senhaVerificadora, String senhaNova){
        if (!senhaVerificadora.equals(senhaUsuario)){
            return false;
        }

        if (senhaNova.equals(senhaUsuario)){
            return false;
        }

        if (!senhaNova.equals(Login.verificarSenha(senhaNova))){
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
