package br.inatel.redesocial.autenticacao;

import br.inatel.redesocial.banco.funcoes.CadastroDAO;
import br.inatel.redesocial.exceptions.CampoObrigatorioException;
import br.inatel.redesocial.exceptions.EmailInvalidoException;
import br.inatel.redesocial.exceptions.FalhaNoCadastroException;
import br.inatel.redesocial.exceptions.SenhaInvalidaException;

public class Cadastro {

    CadastroDAO cadastroDAO = new CadastroDAO();

    public Cadastro(CadastroDAO cadastroDAO) {
        this.cadastroDAO = cadastroDAO;
    }

    public Usuario criarUsuario(String email, String senha, Login login){

        if (email == null || email.isBlank() || senha == null || senha.isBlank())
            throw new CampoObrigatorioException("E-mail e senha são obrigatórios");

        if(verificacoesParaCriacao(email, senha, login)) {
            cadastrarNoBanco(email, senha);
        }

        return new Usuario(email, senha);
    }

    private boolean verificacoesParaCriacao(String email, String senha, Login login){

        if(login.verificarEmail(email).equals("Email inválido")) {
            throw new EmailInvalidoException(
                    "Formato de email inválido");
        }

        if (login.verificarSenha(senha).equals("Senha inválida")) {
            throw new SenhaInvalidaException(
                    "Formato de senha inválida");
        }

        return true;
    }

    private boolean cadastrarNoBanco(String email, String senha){
        boolean sucesso;

        sucesso = cadastroDAO.insertUsuario(email, senha);

        if(sucesso) {
            return true;
        } else {
            throw new FalhaNoCadastroException("Falha ao realizar cadastro.");
        }
    }
}
