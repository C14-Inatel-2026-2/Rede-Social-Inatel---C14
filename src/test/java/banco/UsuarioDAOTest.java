package banco;

import br.inatel.redesocial.banco.funcoes.UsuarioDAO;
import br.inatel.redesocial.usuario.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void testeBuscarPorEmailRetornaUsuario() throws SQLException {
        usuarioEncontrado();

        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        assertEquals(email, usuario.getEmail());
        assertEquals(nome, usuario.getNome());
        verify(connection).prepareStatement(contains("SELECT email, nome FROM usuario"));
        verify(selectStatement).setString(1, email);
    }

    @Test
    public void testeBuscarFechaRecursosNoSucesso() throws SQLException {
        usuarioEncontrado();

        usuarioDAO.buscarPorEmail(email);

        verify(resultSet).close();
        verify(selectStatement).close();
        verify(connection).close();
    }

    @Test
    public void testeBuscarEmailNaoCadastrado() throws SQLException {
        prepararConsulta();
        when(resultSet.next()).thenReturn(false);

        assertNull(usuarioDAO.buscarPorEmail("naoexiste@inatel.br"));
        verify(resultSet, never()).getString(anyString());
    }

    @Test
    public void testeBuscarSemConexao() {
        bancoIndisponivel();

        assertNull(usuarioDAO.buscarPorEmail(email));
        verifyNoInteractions(selectStatement);
    }

    @Test
    public void testeBuscarFalhaNaConsulta() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(selectStatement);
        SQLException erroSQL = new SQLException("Erro na consulta");
        when(selectStatement.executeQuery()).thenThrow(erroSQL);

        assertNull(usuarioDAO.buscarPorEmail(email));
        verify(selectStatement).close();
        verify(connection).close();
    }

    @Test
    public void testeBuscarFalhaAoPrepararStatement() throws SQLException {
        SQLException erroSQL = new SQLException("Erro ao preparar");
        when(connection.prepareStatement(anyString())).thenThrow(erroSQL);

        assertNull(usuarioDAO.buscarPorEmail(email));
        verify(connection).close();
        verifyNoInteractions(selectStatement);
    }

    @Test
    public void testeBuscarFalhaAoFecharConexao() throws SQLException {
        usuarioEncontrado();
        SQLException erroSQL = new SQLException("Erro ao fechar conexão");

        doThrow(erroSQL)
                .when(connection).close();

        assertDoesNotThrow(
                () -> usuarioDAO.buscarPorEmail(email)
        );

        verify(connection).close();
    }

    @Test
    public void testeBuscarFalhaAoFecharResultSet() throws SQLException {
        usuarioEncontrado();
        SQLException erroSQL = new SQLException("Erro ao fechar ResultSet");

        doThrow(erroSQL)
                .when(resultSet).close();

        assertNull(usuarioDAO.buscarPorEmail(email));

        verify(resultSet).close();
        verify(connection).close();
    }

    @Test
    public void testeAlterarSenhaComSucesso() throws SQLException {
        prepararSelectEUpdate(hashDaSenhaAtual);
        when(updateStatement.executeUpdate()).thenReturn(1);

        assertTrue(usuarioDAO.alterarSenha(email, senhaAtual, senhaNova));
        verify(connection).prepareStatement(contains("SELECT senha FROM usuario"));
        verify(connection).prepareStatement(contains("UPDATE usuario SET senha"));
        verify(selectStatement).setString(1, email);
        verify(updateStatement).setString(2, email);
    }

    @Test
    public void testeAlterarSenhaSalvaHashDaNovaSenha() throws SQLException {
        prepararSelectEUpdate(hashDaSenhaAtual);
        when(updateStatement.executeUpdate()).thenReturn(1);

        usuarioDAO.alterarSenha(email, senhaAtual, senhaNova);

        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
        verify(updateStatement).setString(eq(1), hashCaptor.capture());
        String hashSalvo = hashCaptor.getValue();

        assertNotEquals(senhaNova, hashSalvo);
        assertTrue(BCrypt.checkpw(senhaNova, hashSalvo));
    }

    @Test
    public void testeAlterarSenhaFechaRecursosNoSucesso() throws SQLException {
        prepararSelectEUpdate(hashDaSenhaAtual);
        when(updateStatement.executeUpdate()).thenReturn(1);

        usuarioDAO.alterarSenha(email, senhaAtual, senhaNova);

        verify(resultSet).close();
        verify(selectStatement).close();
        verify(updateStatement).close();
        verify(connection).close();
    }

    @Test
    public void testeAlterarSenhaFalhaComSenhaAtualIncorreta() throws SQLException {
        bancoRetornaHash(hashDaSenhaAtual);

        assertFalse(usuarioDAO.alterarSenha(email, senhaErrada, senhaNova));
        verify(connection, never()).prepareStatement(contains("UPDATE"));
        verify(connection).close();
    }

    @Test
    public void testeAlterarSenhaFalhaComEmailNaoCadastrado() throws SQLException {
        prepararConsulta();
        when(resultSet.next()).thenReturn(false);

        assertFalse(usuarioDAO.alterarSenha("naoexiste@inatel.br", senhaAtual, senhaNova));
        verify(connection, never()).prepareStatement(contains("UPDATE"));
        verify(connection).close();
    }

    @Test
    public void testeAlterarSenhaFalhaComHashNuloNoBanco() throws SQLException {
        bancoRetornaHash(null);

        assertFalse(usuarioDAO.alterarSenha(email, senhaAtual, senhaNova));
        verify(connection, never()).prepareStatement(contains("UPDATE"));
        verify(connection).close();
    }

    @Test
    public void testeAlterarSenhaFalhaQuandoNenhumaLinhaEAtualizada() throws SQLException {
        prepararSelectEUpdate(hashDaSenhaAtual);
        when(updateStatement.executeUpdate()).thenReturn(0);

        assertFalse(usuarioDAO.alterarSenha(email, senhaAtual, senhaNova));
        verify(updateStatement).close();
        verify(connection).close();
    }

    @Test
    public void testeAlterarSenhaFalhaSemConexao() {
        bancoIndisponivel();

        assertFalse(usuarioDAO.alterarSenha(email, senhaAtual, senhaNova));
        verifyNoInteractions(selectStatement, updateStatement);
    }

    @Test
    public void testeAlterarSenhaFalhaNoSelect() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(selectStatement);
        SQLException erroSQL = new SQLException("Erro na consulta");
        when(selectStatement.executeQuery()).thenThrow(erroSQL);

        assertFalse(usuarioDAO.alterarSenha(email, senhaAtual, senhaNova));
        verify(selectStatement).close();
        verify(connection).close();
    }

    @Test
    public void testeAlterarSenhaFalhaNoUpdate() throws SQLException {
        prepararSelectEUpdate(hashDaSenhaAtual);
        SQLException erroSQL = new SQLException("Erro no update");
        when(updateStatement.executeUpdate()).thenThrow(erroSQL);

        assertFalse(usuarioDAO.alterarSenha(email, senhaAtual, senhaNova));
        verify(updateStatement).close();
        verify(connection).close();
    }
}
