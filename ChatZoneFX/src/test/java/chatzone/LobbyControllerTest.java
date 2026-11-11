package chatzone;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(JavaFxExtension.class)
public class LobbyControllerTest {

    private static final String EMAIL = "user@email.com";

    private LobbyController controller;
    private Label profileEmailLabel;
    private StackPane profileModal;
    private FlowPane roomsGrid;

    @BeforeEach
    public void setup() throws Exception {
        FXMLLoader loader = new FXMLLoader(LobbyController.class.getResource("lobby.fxml"));
        loader.load();

        controller = loader.getController();
        profileEmailLabel = (Label) loader.getNamespace().get("profileEmailLabel");
        profileModal = (StackPane) loader.getNamespace().get("profileModal");
        roomsGrid = (FlowPane) loader.getNamespace().get("roomsGrid");
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

}