package br.inatel.redesocial.banco.funcoes;

import br.inatel.redesocial.autenticacao.Usuario;
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
}