package br.inatel.redesocial.banco.funcoes;

import br.inatel.redesocial.usuario.Usuario;
import br.inatel.redesocial.banco.ConnectionDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO extends ConnectionDAO {

    public Usuario buscarPorEmail(String email) {
        connectToDb();
        if (connection == null) {
            return null;
        }

        String sql = "SELECT email, nome FROM usuario WHERE email = ?";

        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setString(1, email);

            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario(rs.getString("email"), null);
                    usuario.setNome(rs.getString("nome"));

                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
        return null;
    }

    public boolean alterarSenha(String email, String senhaAtual, String senhaNova) {
        connectToDb();
        if (connection == null) {
            return false;
        }

        String selectSql = "SELECT senha FROM usuario WHERE email = ?";
        String updateSql = "UPDATE usuario SET senha = ? WHERE email = ?";

        try {
            String hashAtual = null;

            try (PreparedStatement p = connection.prepareStatement(selectSql)) {
                p.setString(1, email);

                try (ResultSet rs = p.executeQuery()) {
                    if (rs.next()) {
                        hashAtual = rs.getString("senha");
                    }
                }
            }

            if (hashAtual == null || !BCrypt.checkpw(senhaAtual, hashAtual)) {
                return false;
            }

            try (PreparedStatement p = connection.prepareStatement(updateSql)) {
                p.setString(1, BCrypt.hashpw(senhaNova, BCrypt.gensalt()));
                p.setString(2, email);

                return p.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.out.print("Erro ao alterar senha: " + e.getMessage());
            return false;
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }
}