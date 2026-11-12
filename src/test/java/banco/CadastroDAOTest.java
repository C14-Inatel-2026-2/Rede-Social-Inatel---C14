package banco;

import br.inatel.redesocial.banco.funcoes.CadastroDAO;
import br.inatel.redesocial.exceptions.EmailJaCadastradoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CadastroDAOTest {

    CadastroDAO cadastroDAO;

    private final String email = "christopher@inatel.br";
    private final String senha = "Chizinho67!";
    private final String nome = "Christopher";

    @Mock
    Connection connection;

    @Mock
    PreparedStatement preparedStatement;

    MockedStatic<DriverManager> driverManager;

    @BeforeEach
    public void setup() {
        driverManager = mockStatic(DriverManager.class, withSettings().lenient());
        driverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(connection);

        this.cadastroDAO = new CadastroDAO();
    }

    @AfterEach
    public void tearDown() {
        driverManager.close();
    }

    private void bancoIndisponivel() {
        SQLException erroSQL = new SQLException("Connection refused");
        driverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString())).thenThrow(erroSQL);
    }

    private void prepararStatement() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    public void testeSucessoNoInsert() throws SQLException {
        prepararStatement();

        boolean inserido = cadastroDAO.insertUsuario(email, senha, nome);

        assertTrue(inserido);
        verify(connection).prepareStatement(contains("INSERT INTO usuario"));
        verify(preparedStatement).setString(1, email);
        verify(preparedStatement).setString(3, nome);
        verify(preparedStatement).execute();
    }

    @Test
    public void testeSenhaSalvaComoHash() throws SQLException {
        prepararStatement();
        ArgumentCaptor<String> senhaSalva = ArgumentCaptor.forClass(String.class);

        cadastroDAO.insertUsuario(email, senha, nome);

        verify(preparedStatement).setString(eq(2), senhaSalva.capture());
        assertNotEquals(senha, senhaSalva.getValue());
        assertTrue(BCrypt.checkpw(senha, senhaSalva.getValue()));
    }

    @Test
    public void testeFechaRecursosNoSucesso() throws SQLException {
        prepararStatement();

        cadastroDAO.insertUsuario(email, senha, nome);

        verify(preparedStatement).close();
        verify(connection).close();
    }

    @Test
    public void testeFalhaSemConexao() {
        bancoIndisponivel();

        assertFalse(cadastroDAO.insertUsuario(email, senha, nome));
        verifyNoInteractions(preparedStatement);
    }

    @Test
    public void testeEmailDuplicado() throws SQLException {
        SQLException erroSQL = new SQLIntegrityConstraintViolationException("Duplicate entry");
        prepararStatement();
        when(preparedStatement.execute()).thenThrow(erroSQL);

        EmailJaCadastradoException excecao = assertThrows(EmailJaCadastradoException.class,
                () -> cadastroDAO.insertUsuario(email, senha, nome));

        assertEquals("E-mail já está em uso", excecao.getMessage());
    }

    @Test
    public void testeFalhaNoBancoPorErroDeSQL() throws SQLException {
        SQLException erroSQL = new SQLException("Erro generico");
        prepararStatement();
        when(preparedStatement.execute()).thenThrow(erroSQL);

        assertFalse(cadastroDAO.insertUsuario(email, senha, nome));
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @Test
    public void testeFalhaAoPrepararStatement() throws SQLException {
        SQLException erroSQL = new SQLException("Erro ao preparar");
        when(connection.prepareStatement(anyString())).thenThrow(erroSQL);

        assertFalse(cadastroDAO.insertUsuario(email, senha, nome));
        verify(connection).close();
        verifyNoInteractions(preparedStatement);
    }

    @Test
    public void testeFalhaAoFecharStatementNaoPropagaExcecao() throws SQLException {
        prepararStatement();
        doThrow(new SQLException("Erro ao fechar statement")).when(preparedStatement).close();

        boolean inserido = assertDoesNotThrow(
                () -> cadastroDAO.insertUsuario(email, senha, nome));

        assertTrue(inserido);
    }
}
