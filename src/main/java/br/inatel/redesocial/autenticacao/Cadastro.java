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

    public Usuario criarUsuario(String email, String senha, String nome, Login login){

        verificacaoCampoObrigatorio(email, senha, nome);

        if(verificacoesParaCriacao(email, senha, login)) {
            cadastrarNoBanco(email, senha, nome);
        }

        return new Usuario(email, senha, nome);
    }

    private void verificacaoCampoObrigatorio(String email, String senha, String nome) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()
                || nome == null || nome.isBlank())
            throw new CampoObrigatorioException("E-mail, senha e nome são obrigatórios");
    }

    private boolean verificacoesParaCriacao(String email, String senha, Login login){

        if(!login.verificarEmail(email)) {
            throw new EmailInvalidoException(
                    "Formato de email inválido");
        }

        if (!login.verificarSenha(senha)) {
            throw new SenhaInvalidaException(
                    "Formato de senha inválida");
        }

        return true;
    }

    private boolean cadastrarNoBanco(String email, String senha, String nome){
        boolean sucesso;

        sucesso = cadastroDAO.insertUsuario(email, senha, nome);

        if(sucesso) {
            return true;
        } else {
            throw new FalhaNoCadastroException("Falha ao realizar cadastro.");
        }
    }
}
