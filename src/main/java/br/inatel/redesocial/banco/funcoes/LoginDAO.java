
package br.inatel.redesocial.banco.funcoes;
import br.inatel.redesocial.banco.ConnectionDAO;

import java.sql.SQLException;

public class LoginDAO extends ConnectionDAO {

    public boolean auteticaUser(String nome, String password){
        boolean autenticado = false;
        connectToDb();
        String sql = "SELECT infos FROM user WHERE password=? AND nome=?";

        try {
            pst = connection.prepareStatement(sql);
            pst.setString(1, password);
            pst.setString(2, nome);
            rs = pst.executeQuery();

            if(rs.next()){
                autenticado = true;
            }
        } catch(SQLException e){
            System.out.println("Falha de Login" + e.getMessage());
        } finally {
            try {
                if(rs != null) rs.close();
                if(pst != null) pst.close();
                if(connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar recursos:" + e.getMessage());
            }
        }

        return autenticado;
    }

}
