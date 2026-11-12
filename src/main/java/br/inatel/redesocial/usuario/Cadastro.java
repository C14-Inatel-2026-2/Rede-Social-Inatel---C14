package br.inatel.redesocial.usuario;

import br.inatel.redesocial.banco.funcoes.CadastroDAO;
import br.inatel.redesocial.exceptions.*;

public class Cadastro {

    CadastroDAO cadastroDAO = new CadastroDAO();

    public Cadastro(CadastroDAO cadastroDAO) {
        this.cadastroDAO = cadastroDAO;
    }

    public Usuario criarUsuario(String email, String senha, String nome){

        verificacaoCampoObrigatorio(email, senha, nome);

        verificacoesParaCriacao(email, senha, nome);

        cadastrarNoBanco(email, senha, nome);

        Usuario usuario = new Usuario(email, senha);
        usuario.setNome(nome);
        return usuario;
    }

    private void verificacaoCampoObrigatorio(String email, String senha, String nome) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()
                || nome == null || nome.isBlank())
            throw new CampoObrigatorioException("E-mail, senha e nome são obrigatórios");
    }

    private boolean verificacoesParaCriacao(String email, String senha, String nome){

        if(!Login.verificarNome(nome)) {
            throw new NomeInvalidoException(
                    "Formato de nome inválido. " +
                            "O nome deve ter de 3 a 16 caracteres: letras, números ou _");
        }

        if(!Login.verificarEmail(email)) {
            throw new EmailInvalidoException(
                    "Formato de email inválido");
        }

        if (!Login.verificarSenha(senha)) {
            throw new SenhaInvalidaException(
                    "Formato de senha inválida. " +
                            "A senha deve ter no mínimo 8 caracteres: " +
                            "letras (maiusculas e minusculas), números " +
                            "e caracteres especiais ($*&@#!)");
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
