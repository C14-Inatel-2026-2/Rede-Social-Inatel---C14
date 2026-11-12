package banco;

import br.inatel.redesocial.banco.funcoes.LoginDAO;
import com.sun.jdi.connect.spi.Connection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.sql.DriverManager;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.withSettings;

public class LoginDAOTest {
    LoginDAO loginDAO;

    @Mock
    Connection connection;

    MockedStatic<DriverManager> driverManager;

    @BeforeEach
    public void setup() {
        driverManager = mockStatic(DriverManager.class, withSettings().lenient());
        driverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(connection);

        this.loginDAO = new LoginDAO();
    }

    @AfterEach
    public void tearDown() {
        driverManager.close();
    }
}
