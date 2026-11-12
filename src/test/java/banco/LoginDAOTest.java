package banco;

import br.inatel.redesocial.banco.funcoes.LoginDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginDAOTest {
    LoginDAO loginDAO;

    private final String email = "christopher@inatel.br";
    private final String nome = "Christopher";
    private final String senha = "Chizinho67!";
    private final String senhaInvalida = "senha123";
    private final String hashInvalido = "senhazinha";
    private final String hashDaSenha = BCrypt.hashpw(senha, BCrypt.gensalt(4));

    @Mock
    Connection connection;

    @Mock
    PreparedStatement preparedStatement;

    @Mock
    ResultSet resultSet;

    MockedStatic<DriverManager> driverManager;

    @BeforeEach
    public void setup() {
        driverManager = mockStatic(DriverManager.class, withSettings().lenient());
        driverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(connection);

        this.loginDAO = new LoginDAO();
    }

    @AfterEach
    public void tearDown() {
        driverManager.close();
    }

    private void bancoIndisponivel() {
        driverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                .thenThrow(new SQLException("Connection refused"));
    }

    private void prepararStatement() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    private void prepararConsulta() throws SQLException {
        prepararStatement();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    private void usuarioExisteNoBanco(String hashSalvo) throws SQLException {
        prepararConsulta();
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("senha")).thenReturn(hashSalvo);
    }

    private void bancoRetornaNome() throws SQLException {
        when(resultSet.getString("nome")).thenReturn(nome);
    }

    @Test
    public void testeAutenticar() throws SQLException {
        usuarioExisteNoBanco(hashDaSenha);
        bancoRetornaNome();

        String resultado = loginDAO.autenticarUser(email, senha);

        assertEquals(nome, resultado);
        verify(connection).prepareStatement(contains("SELECT nome, senha FROM usuario"));
        verify(preparedStatement).setString(1, email);
    }

    @Test
    public void testeFechaRecursosNoSucesso() throws SQLException {
        usuarioExisteNoBanco(hashDaSenha);

        loginDAO.autenticarUser(email, senha);

        verify(resultSet).close();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @Test
    public void testeFalhaComSenhaIncorreta() throws SQLException {
        usuarioExisteNoBanco(hashDaSenha);

        assertNull(loginDAO.autenticarUser(email, senhaInvalida));
        verify(resultSet, never()).getString("nome");
    }
}
