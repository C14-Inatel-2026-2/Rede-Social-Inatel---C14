package br.inatel.redesocial.banco;

import java.sql.*;

public abstract class ConnectionDAO {

    protected Connection connection;

    protected PreparedStatement pst;
    protected Statement st;
    protected ResultSet rs;

    private static final String user = System.getenv().getOrDefault("DB_USER", "root");
    private static final String password = System.getenv().getOrDefault("DB_PASSWORD", "root");
    private static final String url = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3307/db");

    public Connection connectToDb() {
        pst = null;
        st = null;
        rs = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com banco de dados: " + e.getMessage());
            connection = null;
        }

        return connection;
    }

}
