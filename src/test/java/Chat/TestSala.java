package Chat;

import br.inatel.redesocial.autenticacao.Usuario;
import br.inatel.redesocial.servidor.Sala;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class TestSala {

    private Sala salaTeste;

    @Mock
    private Usuario usuario;

    @BeforeEach
    void criarSala() {
        salaTeste = new Sala(12, "teste");
    }

    private void lotarSala() {
        for (int i = 0; i < salaTeste.getCapacidadeMaxima(); i++) {
            salaTeste.addUsuario(mock(Usuario.class));
        }
    }

    @Test
    public void salaVazia() {
        assertEquals(0, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void salaVagasDisponiveis() {
        assertTrue(salaTeste.getQuantidadeUsuarios() < salaTeste.getCapacidadeMaxima());
    }

    @Test
    public void addUsuario() {
        salaTeste.addUsuario(usuario);
        assertEquals(1, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void removeUsuario() {
        salaTeste.addUsuario(usuario);
        salaTeste.removeUsuario(usuario);
        assertEquals(0, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void guardaCapacidadeMaxima() {
        assertEquals(12, salaTeste.getCapacidadeMaxima());
    }

    @Test
    public void listaDeUsuariosComecaVazia() {
        assertTrue(salaTeste.getUsuariosAtivos().isEmpty());
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
        assertTrue(salaTeste.addUsuario(usuario));
    }

    @Test
    public void addUsuarioGuardaUsuarioNaLista() {
        salaTeste.addUsuario(usuario);
        assertTrue(salaTeste.getUsuariosAtivos().contains(usuario));
    }

    @Test
    public void addUsuarioRetornaFalseQuandoLotada() {
        lotarSala();
        assertFalse(salaTeste.addUsuario(usuario));
    }

    @Test
    public void addUsuarioNaoIncrementaQuandoLotada() {
        lotarSala();
        salaTeste.addUsuario(usuario);
        assertEquals(12, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void addUsuarioNaoGuardaUsuarioQuandoLotada() {
        lotarSala();
        salaTeste.addUsuario(usuario);
        assertFalse(salaTeste.getUsuariosAtivos().contains(usuario));
    }

    @Test
    public void salaNaoEstaLotadaComVagas() {
        salaTeste.addUsuario(usuario);
        assertFalse(salaTeste.isLotada());
    }

    @Test
    public void salaEstaLotadaNaCapacidadeMaxima() {
        lotarSala();
        assertTrue(salaTeste.isLotada());
    }

    @Test
    public void removeUsuarioTiraDaLista() {
        salaTeste.addUsuario(usuario);
        salaTeste.removeUsuario(usuario);
        assertFalse(salaTeste.getUsuariosAtivos().contains(usuario));
    }

    @Test
    public void removeUsuarioEmSalaVaziaNaoFicaNegativo() {
        salaTeste.removeUsuario(usuario);
        assertEquals(0, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void removeUsuarioLiberaVaga() {
        lotarSala();
        Usuario ultimo = salaTeste.getUsuariosAtivos().get(0);
        salaTeste.removeUsuario(ultimo);
        assertFalse(salaTeste.isLotada());
        assertTrue(salaTeste.addUsuario(usuario));
    }

    @Test
    public void removeUsuarioQueNaoEstaNaSalaNaoAlteraQuantidade() {
        salaTeste.addUsuario(usuario);
        salaTeste.removeUsuario(mock(Usuario.class));
        assertEquals(1, salaTeste.getQuantidadeUsuarios());
    }

    @Test
    public void removeUsuarioQueNaoEstaNaSalaMantemListaIntacta() {
        salaTeste.addUsuario(usuario);
        salaTeste.removeUsuario(mock(Usuario.class));
        assertTrue(salaTeste.getUsuariosAtivos().contains(usuario));
    }
}