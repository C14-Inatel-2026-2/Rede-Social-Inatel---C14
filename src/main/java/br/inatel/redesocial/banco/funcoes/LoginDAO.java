
package br.inatel.redesocial.banco.funcoes;
import br.inatel.redesocial.banco.ConnectionDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class LoginDAO extends ConnectionDAO {

    public String autenticarUser(String email, String password){
        String nome = null;
        connectToDb();
        if (connection == null) {
            return null;
        }

        String sql = "SELECT nome FROM usuario WHERE email=?";

        try {
            pst = connection.prepareStatement(sql);
            pst.setString(1, email);
            rs = pst.executeQuery();

            if(rs.next()){
                String hashSalvo = rs.getString("senha");

                if(hashSalvo != null && BCrypt.checkpw(password, hashSalvo)) {
                    nome = rs.getString("nome");
                }
            }
        } catch(SQLException e){
            System.out.println("Falha de Login: " + e.getMessage());
        } finally {
            try {
                if(rs != null) rs.close();
                if(pst != null) pst.close();
                if(connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }

        return nome;
    }

}
