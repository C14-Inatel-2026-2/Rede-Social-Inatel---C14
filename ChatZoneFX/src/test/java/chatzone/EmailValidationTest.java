package chatzone;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailValidationTest {

    @Test
    void emailValido_retornaTrue() {
        AuthService auth = Mockito.spy(new AuthService());
        assertTrue(auth.isValidEmail("usuario@email.com"));
    }

    @Test
    void emailSemArroba_retornaFalse() {
        AuthService auth = Mockito.spy(new AuthService());
        assertFalse(auth.isValidEmail("usuarioemail.com"));
    }

    @Test
    void emailComMaiuscula_retornaFalse() {
        AuthService auth = Mockito.spy(new AuthService());
        assertFalse(auth.isValidEmail("Usuario@Email.com"));
    }

    @Test
    void emailNulo_retornaFalse() {
        AuthService auth = Mockito.spy(new AuthService());
        assertFalse(auth.isValidEmail(null));
    }

    @Test
    void loginComEmailInvalido_lancaExcecao() {
        AuthService auth = mock(AuthService.class);
        doThrow(new IllegalArgumentException("Email inválido"))
                .when(auth).login("invalido", "Senha@123");

        assertThrows(IllegalArgumentException.class,
                () -> auth.login("invalido", "Senha@123"));
        verify(auth).login("invalido", "Senha@123");
    }

    @Test
    void loginComEmailValido_retornaLoginOk() {
        AuthService auth = mock(AuthService.class);
        when(auth.login("user@email.com", "Senha@123")).thenReturn("LOGIN_OK");

        assertEquals("LOGIN_OK", auth.login("user@email.com", "Senha@123"));
        verify(auth).login("user@email.com", "Senha@123");
    }
}
