package br.inatel.redesocial.banco;

import java.sql.*;

public abstract class ConnectionDAO {

    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3307/db";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "root";

    protected Connection connection;

    protected PreparedStatement pst;
    protected Statement st;
    protected ResultSet rs;

    private static final String user = envOrDefault("DB_USER", DEFAULT_DB_USER);
    private static final String password = envOrDefault("DB_PASSWORD", DEFAULT_DB_PASSWORD);
    private static final String url = envOrDefault("DB_URL", DEFAULT_DB_URL);

    private static String envOrDefault(String name, String defaultValue) {
        return System.getenv().getOrDefault(name, defaultValue);
    }

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
