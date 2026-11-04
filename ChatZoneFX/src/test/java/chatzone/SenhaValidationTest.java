package chatzone;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SenhaValidationTest {

    @Test
    void senhaValida_retornaTrue() {
        AuthService auth = Mockito.spy(new AuthService());
        assertTrue(auth.isValidSenha("Senha@123"));
    }

    @Test
    void senhaSemMaiuscula_retornaFalse() {
        AuthService auth = Mockito.spy(new AuthService());
        assertFalse(auth.isValidSenha("senha@123"));
    }

    @Test
    void senhaSemEspecial_retornaFalse() {
        AuthService auth = Mockito.spy(new AuthService());
        assertFalse(auth.isValidSenha("Senha1234"));
    }

    @Test
    void senhaCurta_retornaFalse() {
        AuthService auth = Mockito.spy(new AuthService());
        assertFalse(auth.isValidSenha("S@1a"));
    }

    @Test
    void senhaNula_retornaFalse() {
        AuthService auth = Mockito.spy(new AuthService());
        assertFalse(auth.isValidSenha(null));
    }

    @Test
    void loginComSenhaInvalida_lancaExcecao() {
        AuthService auth = mock(AuthService.class);
        doThrow(new IllegalArgumentException("Senha inválida"))
                .when(auth).login("user@email.com", "fraca");

        assertThrows(IllegalArgumentException.class,
                () -> auth.login("user@email.com", "fraca"));
        verify(auth).login("user@email.com", "fraca");
    }

    @Test
    void loginComSenhaValida_retornaLoginOk() {
        AuthService auth = mock(AuthService.class);
        when(auth.login("user@email.com", "Senha@123")).thenReturn("LOGIN_OK");

        assertEquals("LOGIN_OK", auth.login("user@email.com", "Senha@123"));
        verify(auth).login("user@email.com", "Senha@123");
    }
}
