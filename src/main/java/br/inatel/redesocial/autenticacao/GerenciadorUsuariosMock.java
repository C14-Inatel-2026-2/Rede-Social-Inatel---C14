package br.inatel.redesocial.autenticacao;

public class GerenciadorUsuariosMock implements GerenciadorUsuarios{
    @Override
    public Usuario buscaUsuarioPorNome(String emailUsuario){
        return new Usuario(emailUsuario, "123");
    }
}
