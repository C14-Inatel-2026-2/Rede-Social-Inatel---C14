package br.inatel.redesocial.usuario;

public class Usuario {
    private final String email;
    private String senhaUsuario;
    private String nome;

    public Usuario(String email, String senha) {
        this.email = email;
        this.senhaUsuario = senha;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
