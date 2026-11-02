package Chat;

import br.inatel.redesocial.autenticacao.Usuario;
import br.inatel.redesocial.servidor.Sala;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TestSala {

    private Sala salaTeste;

    @Mock
    private Usuario usuario;

    @BeforeEach
    void criarSala() {
        salaTeste = new Sala(12, "teste");
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
}