package br.inatel.redesocial.banco.funcoes;

import br.inatel.redesocial.banco.ConnectionDAO;
import br.inatel.redesocial.exceptions.EmailJaCadastradoException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class CadastroDAO extends ConnectionDAO {

    public boolean insertUsuario(String email, String senha) {
        connectToDb();
        String sql = "INSERT INTO usuario(email, senha) VALUES(?, ?)";

        try {
            pst = connection.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, senha);
            pst.execute();

            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new EmailJaCadastradoException("E-mail já está em uso");

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar usuário no banco: " + e.getMessage());

            return false;
        } finally {

            try {
                if (pst != null) pst.close();
                if (connection != null) connection.close();
            } catch (SQLException e){
                System.out.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }

}
