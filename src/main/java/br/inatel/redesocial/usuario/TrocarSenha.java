package br.inatel.redesocial.usuario;

import br.inatel.redesocial.banco.funcoes.UsuarioDAO;
import br.inatel.redesocial.exceptions.CampoObrigatorioException;
import br.inatel.redesocial.exceptions.SenhaInvalidaException;

public class TrocarSenha {
    private final UsuarioDAO usuarioDAO;

    public TrocaSenha(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public boolean alterarSenha(Usuario usuario, String senhaAtual, String senhaNova) {
        if (senhaAtual == null || senhaAtual.isBlank() || senhaNova == null || senhaNova.isBlank()) {
            throw new CampoObrigatorioException("Senha atual e nova senha são obrigatórias");
        }

        if (!Login.verificarSenha(senhaNova)) {
            throw new SenhaInvalidaException("Formato de senha inválida. " + "A senha deve ter no mínimo 8 caracteres: " + "letras (maiusculas e minusculas), números " + "e caracteres especiais ($*&@#!)");
        }

        if (senhaNova.equals(senhaAtual)) {
            throw new SenhaInvalidaException("A nova senha deve ser diferente da atual");
        }

        return usuarioDAO.alterarSenha(usuario.getEmail(), senhaAtual, senhaNova);
    }
}
