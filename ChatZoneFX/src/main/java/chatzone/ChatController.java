package chatzone;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ChatController {

    @FXML private Label      roomNameLabel;
    @FXML private Label      usersCountLabel;
    @FXML private VBox       messagesBox;
    @FXML private ScrollPane messagesScroll;
    @FXML private TextArea   msgInput;
    @FXML private Button     sendBtn;

    private String currentEmail;
    private String roomName;
    private int    roomId;
    private int    port;

    // TODO: campo ChatSocket socket;

    public void init(String roomName, int roomId, int port, String email) {
        this.roomName     = roomName;
        this.roomId       = roomId;
        this.port         = port;
        this.currentEmail = email;

        roomNameLabel.setText(roomName);
        usersCountLabel.setText("conectando…");
        sendBtn.setDisable(true);

        // Enter envia, Shift+Enter quebra linha
        msgInput.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER && !e.isShiftDown()) {
                e.consume();
                onSend();
            }
        });

        // TODO: conectar socket
        demoMode();
    }

    private void demoMode() {
        usersCountLabel.setText("5/12 online");
        sendBtn.setDisable(false);
        appendSystem("Você entrou em " + roomName + " [modo demo]");
        appendReceived("joao@email.com", "Oi pessoal! 👋");
        appendReceived("maria@email.com", "Olá! Bem-vindo!");
    }

    @FXML
    void onSend() {
        String text = msgInput.getText().trim();
        if (text.isEmpty()) return;
        // TODO: enviar via socket
        appendSent(currentEmail, text);
        msgInput.clear();
    }

    @FXML
    void onBack() {
        // TODO: fechar socket
        try { Main.showLobby(currentEmail); } catch (Exception ignored) {}
    }

    void appendSystem(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("msg-system");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setAlignment(Pos.CENTER);
        messagesBox.getChildren().add(lbl);
        scrollToBottom();
    }

    void appendReceived(String author, String text) {
        VBox bubble = buildBubble(author, text, false);
        HBox row = new HBox(bubble);
        row.setAlignment(Pos.CENTER_LEFT);
        messagesBox.getChildren().add(row);
        scrollToBottom();
    }

    void appendSent(String author, String text) {
        VBox bubble = buildBubble(author, text, true);
        HBox row = new HBox(bubble);
        row.setAlignment(Pos.CENTER_RIGHT);
        messagesBox.getChildren().add(row);
        scrollToBottom();
    }

    private VBox buildBubble(String author, String text, boolean sent) {
        Label authorLbl = new Label(author);
        authorLbl.getStyleClass().add(sent ? "msg-author-sent" : "msg-author");

        Label textLbl = new Label(text);
        textLbl.setWrapText(true);
        textLbl.setMaxWidth(480);
        textLbl.getStyleClass().add("msg-text");

        VBox bubble = new VBox(3, authorLbl, textLbl);
        bubble.getStyleClass().add(sent ? "msg-sent" : "msg-received");
        bubble.setMaxWidth(520);
        return bubble;
    }

    private void scrollToBottom() {
        Platform.runLater(() -> messagesScroll.setVvalue(1.0));
    }
}
