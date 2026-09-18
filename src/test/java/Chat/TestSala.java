package Chat;

import br.inatel.redesocial.autenticacao.Usuario;
import br.inatel.redesocial.servidor.Sala;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSala {
    @Test
    public void salaVazia(){
        Sala salaTeste = new Sala(12);
        assertTrue(salaTeste.getQuantidadeUsuarios()==0);
    }
    @Test
    public void salaVagasDisponiveis(){
        Sala salaTeste = new Sala(12);
        assertTrue(salaTeste.getQuantidadeUsuarios()<salaTeste.getCapacidadeMaxima());
    }
    @Test
    public void AddUsuario(){
        Sala salaTeste = new Sala(12);
        Usuario usuario = new MockUsuario("teste@email.com", "teste");
        salaTeste.addUsuario(usuario);
        assertTrue(salaTeste.getQuantidadeUsuarios()==1);
    }
    @Test
    public void RemoveUsuario(){
        Sala salaTeste = new Sala(12);
        Usuario usuario = new MockUsuario("teste@email.com", "teste");
        salaTeste.addUsuario(usuario);
        salaTeste.removeUsuario(usuario);
        assertTrue(salaTeste.getQuantidadeUsuarios()==0);
    }

}
