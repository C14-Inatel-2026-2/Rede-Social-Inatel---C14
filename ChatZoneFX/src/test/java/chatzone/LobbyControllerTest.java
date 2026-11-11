package chatzone;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import java.util.concurrent.CountDownLatch;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(JavaFxExtension.class)
public class LobbyControllerTest {

    private static final String EMAIL = "user@email.com";

    @Mock
    private LobbyController.BuscadorDeSalas buscadorDeSalas;

    private LobbyController controller;
    private Label profileEmailLabel;
    private StackPane profileModal;
    private CountDownLatch salasCarregadas;
    private FlowPane roomsGrid;

    @BeforeEach
    public void setup() throws Exception {
        Session.clear();
        MockitoAnnotations.openMocks(this);
        when(buscadorDeSalas.buscar()).thenReturn(new LobbyController.Room[] {
                new LobbyController.Room(0, "Sala Geral", 5, 12, 0),
                new LobbyController.Room(0, "Sala Jogos", 12, 12, 0),
                new LobbyController.Room(0, "Sala Música", 0, 12, 0)
        });

        controller = new LobbyController(buscadorDeSalas);

        FXMLLoader loader = new FXMLLoader(LobbyController.class.getResource("lobby.fxml"));
        loader.setControllerFactory(tipo -> controller);
        loader.load();

        profileEmailLabel = (Label) loader.getNamespace().get("profileEmailLabel");
        profileModal = (StackPane) loader.getNamespace().get("profileModal");
        roomsGrid = (FlowPane) loader.getNamespace().get("roomsGrid");


        salasCarregadas = new CountDownLatch(1);
        roomsGrid.getChildren().addListener((ListChangeListener<Node>) mudanca -> {
            if (roomsGrid.getChildren().size() == 3) salasCarregadas.countDown();
        });
    }

    @Test
    public void testeLobbyComecaSemSalas() {
        assertEquals(0, roomsGrid.getChildren().size());
    }

    @Test
    public void testeModalDePerfilComecaFechado() {
        assertFalse(profileModal.isVisible());
    }

    @Test
    public void testeInitMostraEmailNoPerfil() {
        controller.init(EMAIL);

        assertEquals(EMAIL, profileEmailLabel.getText());
    }

    @Test
    public void testeBotaoDeUsuarioAbrePerfil() {
        controller.onUserBtn();

        assertTrue(profileModal.isVisible());
    }

    @Test
    public void testeLogoutFechaPerfil() {
        controller.onUserBtn();

        controller.onLogout();

        assertFalse(profileModal.isVisible());
    }

    @Test
    public void testeCardMostraUsuariosOnline() throws Exception {
        controller.init(EMAIL);
        salasCarregadas.await(5, TimeUnit.SECONDS);

        VBox card = (VBox) roomsGrid.getChildren().get(0);
        HBox meta = (HBox) card.getChildren().get(1);
        Label contador = (Label) meta.getChildren().get(1);

        assertEquals("5/12 online", contador.getText());
    }

    @Test
    public void testeInitCarregaTodasAsSalas() throws Exception {
        controller.init(EMAIL);
        salasCarregadas.await(5, TimeUnit.SECONDS);

        assertEquals(3, roomsGrid.getChildren().size());
    }

    @Test
    public void testeSalaLotadaMarcadaComoCheia() throws Exception {
        controller.init(EMAIL);
        assertTrue(salasCarregadas.await(5, TimeUnit.SECONDS));

        VBox salaJogos = (VBox) roomsGrid.getChildren().get(1);

        assertTrue(salaJogos.getStyleClass().contains("room-card-full"));
    }

    @Test
    public void testeSalaLotadaNaoPodeSerClicada() throws Exception {
        controller.init(EMAIL);
        assertTrue(salasCarregadas.await(5, TimeUnit.SECONDS));

        VBox salaJogos = (VBox) roomsGrid.getChildren().get(1);

        assertNull(salaJogos.getOnMouseClicked());
    }
}