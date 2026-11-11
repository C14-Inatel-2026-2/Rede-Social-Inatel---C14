package chatzone;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(JavaFxExtension.class)
public class ChatControllerTest {

    private ChatController controller;
    private Label roomNameLabel;
    private Label usersCountLabel;
    private VBox messagesBox;
    private ScrollPane messagesScroll;
    private TextArea msgInput;
    private Button sendBtn;

    @BeforeEach
    public void setup() throws Exception {
        FXMLLoader loader = new FXMLLoader(ChatController.class.getResource("chat.fxml"));
        loader.load();

        controller = loader.getController();
        roomNameLabel = (Label) loader.getNamespace().get("roomNameLabel");
        usersCountLabel = (Label) loader.getNamespace().get("usersCountLabel");
        messagesBox = (VBox) loader.getNamespace().get("messagesBox");
        messagesScroll = (ScrollPane) loader.getNamespace().get("messagesScroll");
        msgInput = (TextArea) loader.getNamespace().get("msgInput");
        sendBtn = (Button) loader.getNamespace().get("sendBtn");
    }

    @Test
    public void testeChatComecaSemMensagens() {
        assertEquals(0, messagesBox.getChildren().size());
    }

    @Test
    public void testeBotaoEnviarComecaDesabilitado() {
        assertTrue(sendBtn.isDisabled());
    }

    @Test
    public void testeStatusComecaConectando() {
        assertEquals("conectando…", usersCountLabel.getText());
    }

    @Test
    public void testeMensagemDoSistemaMostraTexto() {
        controller.appendSystem("Você entrou em Sala Geral");

        Label mensagem = (Label) messagesBox.getChildren().get(0);

        assertEquals("Você entrou em Sala Geral", mensagem.getText());
    }

    @Test
    public void testeMensagemDoSistemaTemEstiloDeSistema() {
        controller.appendSystem("Você entrou em Sala Geral");

        Label mensagem = (Label) messagesBox.getChildren().get(0);

        assertTrue(mensagem.getStyleClass().contains("msg-system"));
    }

}
