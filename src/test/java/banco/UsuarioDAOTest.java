package banco;

import br.inatel.redesocial.banco.funcoes.UsuarioDAO;
import br.inatel.redesocial.usuario.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioDAOTest {

    UsuarioDAO usuarioDAO;

    private final String email = "christopher@inatel.br";
    private final String nome = "Christopher";
    private final String senhaAtual = "Chizinho67!";
    private final String senhaNova = "NovaSenha123!";
    private final String senhaErrada = "senha123";
    private final String hashInvalido = "senhazinha";
    private final String hashDaSenhaAtual = BCrypt.hashpw(senhaAtual, BCrypt.gensalt(4));

    @Mock
    Connection connection;

    @Mock
    PreparedStatement selectStatement;

    @Mock
    PreparedStatement updateStatement;

    @Mock
    ResultSet resultSet;

    MockedStatic<DriverManager> driverManager;

    @BeforeEach
    public void setup() {
        driverManager = mockStatic(DriverManager.class, withSettings().lenient());
        driverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(connection);

        this.usuarioDAO = new UsuarioDAO();
    }

    @AfterEach
    public void tearDown() {
        driverManager.close();
    }

    private void bancoIndisponivel() {
        SQLException erroSQL = new SQLException("Connection refused");
        driverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                .thenThrow(erroSQL);
    }

    private void prepararConsulta() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(selectStatement);
        when(selectStatement.executeQuery()).thenReturn(resultSet);
    }

    private void usuarioEncontrado() throws SQLException {
        prepararConsulta();
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("email")).thenReturn(email);
        when(resultSet.getString("nome")).thenReturn(nome);
    }

    private void bancoRetornaHash(String hashSalvo) throws SQLException {
        prepararConsulta();
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("senha")).thenReturn(hashSalvo);
    }

    private void prepararSelectEUpdate(String hashSalvo) throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(selectStatement, updateStatement);
        when(selectStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("senha")).thenReturn(hashSalvo);
    }
}
