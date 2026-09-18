package br.inatel.redesocial.banco;

import java.sql.*;

public abstract class ConnectionDAO {

    protected Connection connection;

    protected PreparedStatement pst;
    protected Statement st;
    protected ResultSet rs;

    protected String database = "exemplo";
    protected String user = "root";
    protected String password = "root";
    protected String url = "jdbc:mysql://localhost:3306/" + database;

    public Connection connectToDb() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com banco de dados:" + e.getMessage());
        }

        return null;
    }

}
