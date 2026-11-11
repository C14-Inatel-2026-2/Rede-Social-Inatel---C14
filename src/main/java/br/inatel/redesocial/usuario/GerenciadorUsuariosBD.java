package br.inatel.redesocial.usuario;

import br.inatel.redesocial.banco.funcoes.UsuarioDAO;

public class GerenciadorUsuariosBD {
    private final UsuarioDAO usuarioDAO;

    public GerenciadorUsuariosBD(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario buscaUsuarioPorEmail(String email) {
        return usuarioDAO.buscarPorEmail(email);
    }
}