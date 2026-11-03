package br.inatel.redesocial.autenticacao;

import br.inatel.redesocial.banco.funcoes.CadastroDAO;
import br.inatel.redesocial.exceptions.FalhaNoCadastroException;

public class Cadastro {

    CadastroDAO cadastroDAO = new CadastroDAO();

    public Cadastro(CadastroDAO cadastroDAO) {
        this.cadastroDAO = cadastroDAO;
    }

    public Usuario criarUsuario(String email, String senha, Login login){

        Usuario usuario = null;
        try {
            verificacoesParaCriacao(email, senha, login);

            boolean sucesso = cadastroDAO.insertUsuario(email, senha);
            if(sucesso) {
                usuario = new Usuario(email, senha);
                usuario.setLogado(true);
            }
        } catch (FalhaNoCadastroException e) {
            System.out.println("Erro:" + e);
        }

        return usuario;
    }

    private boolean verificacoesParaCriacao(String email, String senha, Login login){

        if(login.verificarEmail(email).equals("Email inválido")) {
            throw new FalhaNoCadastroException(
                    "Cadastro inválido por conta de email inválido");
        }

        if (login.verificarSenha(senha).equals("Senha inválida")) {
            throw new FalhaNoCadastroException(
                    "Cadastro inválido por conta de senha fora do padrão de segurança");
        }

        return true;
    }
}
