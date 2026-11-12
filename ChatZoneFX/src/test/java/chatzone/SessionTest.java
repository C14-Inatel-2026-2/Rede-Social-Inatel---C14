package chatzone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SessionTest {

    private static final String EMAIL = "user@email.com";
    private static final String NOME = "user_01";

    @BeforeEach
    public void setup() {
        Session.clear();
    }

    @Test
    public void testeSessaoComecaSemUsuario() {
        assertNull(Session.email());
    }

    @Test
    public void testeSessaoGuardaEmailDoUsuarioLogado() {
        Session.setUser(EMAIL, NOME);

        assertEquals(EMAIL, Session.email());
    }

    @Test
    public void testeSessaoGuardaNomeDoUsuarioLogado() {
        Session.setUser(EMAIL, NOME);

        assertEquals(NOME, Session.nome());
    }

    @Test
    public void testeLogoutLimpaASessao() {
        Session.setUser(EMAIL, NOME);

        Session.clear();

        assertNull(Session.email());
    }
}