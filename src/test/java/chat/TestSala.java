package chat;

import br.inatel.redesocial.usuario.Usuario;
import br.inatel.redesocial.servidor.Sala;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@ResourceLock("server-static-state")
public class TestSala {

    private Sala salaTeste;

    @Mock
    private Usuario usuario;
    @Mock
    private PrintWriter escritor;

    @BeforeEach
    void criarSala() {
        salaTeste = new Sala(12, "teste");
    }

    private void lotarSala() {
        for (int i = 0; i < salaTeste.getCapacidadeMaxima(); i++) {
            salaTeste.addUsuario(new Usuario("usuario" + i, "senha"), mock(PrintWriter.class));
        }
    }

    @Test
    public void salaVazia() {
        assertEquals(0, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void guardaCapacidadeMaxima() {
        assertEquals(12, salaTeste.getCapacidadeMaxima());
    }

    @Test
    public void guardaNome() {
        assertEquals("teste", salaTeste.getNome());
    }

    @Test
    public void listaDeUsuariosComecaVazia() {
        assertTrue(salaTeste.getUsuariosAtivos().isEmpty());
    }

    @Test
    public void escritoresComecamVazios() {
        assertTrue(salaTeste.getEscritores().isEmpty());
    }

    @Test
    public void contaSalasCriadas() {
        int antes = Sala.getQuantidadeSalas();
        new Sala(5, "a");
        new Sala(5, "b");
        assertEquals(antes + 2, Sala.getQuantidadeSalas());
    }

    @Test
    public void addUsuarioRetornaTrueQuandoHaVaga() {
        assertTrue(salaTeste.addUsuario(usuario, escritor));
    }

    @Test
    public void addUsuarioNuloRetornaFalseENaoAlteraSala() {
        assertFalse(salaTeste.addUsuario((Usuario) null, escritor));

        assertEquals(0, salaTeste.getQuantidadeUsuarios());
        assertTrue(salaTeste.getUsuariosAtivos().isEmpty());
        assertTrue(salaTeste.getEscritores().isEmpty());
    }

    @Test
    public void addUsuarioIncrementaQuantidade() {
        salaTeste.addUsuario(usuario, escritor);
        assertEquals(1, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void addUsuarioGuardaUsuarioNaLista() {
        salaTeste.addUsuario(usuario, escritor);
        assertTrue(salaTeste.getUsuariosAtivos().contains(usuario));
    }

    @Test
    public void addUsuarioGuardaEscritor() {
        salaTeste.addUsuario(usuario, escritor);
        assertTrue(salaTeste.getEscritores().contains(escritor));
    }

    @Test
    public void addUsuarioRetornaFalseQuandoLotada() {
        lotarSala();
        assertFalse(salaTeste.addUsuario(usuario, escritor));
    }

    @Test
    public void addUsuarioNaoIncrementaQuandoLotada() {
        lotarSala();
        salaTeste.addUsuario(usuario, escritor);
        assertEquals(12, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void addUsuarioNaoGuardaUsuarioNemEscritorQuandoLotada() {
        lotarSala();
        salaTeste.addUsuario(usuario, escritor);
        assertFalse(salaTeste.getUsuariosAtivos().contains(usuario));
        assertFalse(salaTeste.getEscritores().contains(escritor));
    }

    @Test
    public void salaNaoEstaLotadaComVagas() {
        salaTeste.addUsuario(usuario, escritor);
        assertFalse(salaTeste.isLotada());
    }

    @Test
    public void salaEstaLotadaNaCapacidadeMaxima() {
        lotarSala();
        assertTrue(salaTeste.isLotada());
    }

    @Test
    public void removeUsuarioDiminuiQuantidade() {
        salaTeste.addUsuario(usuario, escritor);
        salaTeste.removeUsuario(usuario);
        assertEquals(0, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void removeUsuarioTiraDaLista() {
        salaTeste.addUsuario(usuario, escritor);
        salaTeste.removeUsuario(usuario);
        assertFalse(salaTeste.getUsuariosAtivos().contains(usuario));
    }

    @Test
    public void removeUsuarioTiraTambemOEscritor() {
        salaTeste.addUsuario(usuario, escritor);
        salaTeste.removeUsuario(usuario);
        assertFalse(salaTeste.getEscritores().contains(escritor));
    }

    @Test
    public void removeUsuarioEmSalaVaziaNaoFicaNegativo() {
        salaTeste.removeUsuario(usuario);
        assertEquals(0, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void removeUsuarioLiberaVaga() {
        lotarSala();
        Usuario primeiro = salaTeste.getUsuariosAtivos().get(0);
        salaTeste.removeUsuario(primeiro);
        assertFalse(salaTeste.isLotada());
        assertTrue(salaTeste.addUsuario(usuario, escritor));
    }

    @Test
    public void removeUsuarioQueNaoEstaNaSalaNaoAlteraQuantidade() {
        salaTeste.addUsuario(usuario, escritor);
        salaTeste.removeUsuario(mock(Usuario.class));
        assertEquals(1, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void removeUsuarioQueNaoEstaNaSalaMantemListaEEscritor() {
        salaTeste.addUsuario(usuario, escritor);
        salaTeste.removeUsuario(mock(Usuario.class));
        assertTrue(salaTeste.getUsuariosAtivos().contains(usuario));
        assertTrue(salaTeste.getEscritores().contains(escritor));
    }

    @Test
    public void removeUsuarioPorEmailRemoveUsuarioCorrespondente() {
        Usuario outroUsuario = mock(Usuario.class);
        org.mockito.Mockito.when(usuario.getEmail()).thenReturn("thiago");
        PrintWriter outroEscritor = mock(PrintWriter.class);
        salaTeste.addUsuario(usuario, escritor);
        salaTeste.addUsuario(outroUsuario, outroEscritor);

        salaTeste.removeUsuario("thiago");

        assertEquals(1, salaTeste.getQuantidadeUsuarios());
        assertFalse(salaTeste.getUsuariosAtivos().contains(usuario));
        assertTrue(salaTeste.getUsuariosAtivos().contains(outroUsuario));
        assertFalse(salaTeste.getEscritores().contains(escritor));
        assertTrue(salaTeste.getEscritores().contains(outroEscritor));
    }

    @Test
    public void removeUsuarioComEmailNuloNaoLancaExcecao() {
        salaTeste.addUsuario(usuario, escritor);

        assertDoesNotThrow(() -> salaTeste.removeUsuario((String) null));

        assertEquals(1, salaTeste.getQuantidadeUsuarios());
        assertTrue(salaTeste.getUsuariosAtivos().contains(usuario));
    }

    @Test
    public void removeUsuarioPorEmailDesconhecidoNaoRemoveUsuario() {
        salaTeste.addUsuario(usuario, escritor);
        org.mockito.Mockito.when(usuario.getEmail()).thenReturn("thiago");

        salaTeste.removeUsuario("desconhecido");

        assertEquals(1, salaTeste.getQuantidadeUsuarios());
        assertTrue(salaTeste.getUsuariosAtivos().contains(usuario));
    }

    @Test
    public void nomesUsuariosAtivosRetornaEmails() {
        Usuario outroUsuario = mock(Usuario.class);
        org.mockito.Mockito.when(usuario.getEmail()).thenReturn("thiago");
        org.mockito.Mockito.when(outroUsuario.getEmail()).thenReturn("outro");
        salaTeste.addUsuario(usuario, escritor);
        salaTeste.addUsuario(outroUsuario, mock(PrintWriter.class));

        assertEquals(List.of("thiago", "outro"), salaTeste.getNomesUsuariosAtivos());
    }

    @Test
    public void escritoresNaoSaoCompartilhadosEntreSalas() {
        Sala outra = new Sala(5, "outra");
        salaTeste.addUsuario(usuario, escritor);
        assertTrue(outra.getEscritores().isEmpty());
    }
}