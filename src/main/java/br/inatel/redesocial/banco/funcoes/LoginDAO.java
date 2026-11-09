
package br.inatel.redesocial.banco.funcoes;
import br.inatel.redesocial.banco.ConnectionDAO;

import java.sql.SQLException;

public class LoginDAO extends ConnectionDAO {

    public String auteticaUser(String email, String password){
        String nome = null;
        connectToDb();
        if (connection == null) {
            return null;
        }

        String sql = "SELECT nome FROM usuario WHERE senha=? AND email=?";

        try {
            pst = connection.prepareStatement(sql);
            pst.setString(1, password);
            pst.setString(2, email);
            rs = pst.executeQuery();

            if(rs.next()){
                nome = rs.getString("nome");
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
