package chatzone;

import br.inatel.redesocial.cliente.ClienteSocket;
import br.inatel.redesocial.cliente.dto.Mensagem;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.net.Socket;

public class ChatController {

    private static final String TIPO_ENTRAR   = "entrar_sala";
    private static final String TIPO_MENSAGEM = "mensagem";
    private static final String TIPO_SAIR     = "sair";
    private static final String TIPO_ERRO     = "erro";

    private static final Gson GSON = new Gson();

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

    private ClienteSocket clienteSocket;
    private Socket        socket;
    private volatile boolean saindo = false;

    public void init(String roomName, int roomId, int port, String email) {
        this.roomName     = roomName;
        this.roomId       = roomId;
        this.port         = port;
        this.currentEmail = email;

        roomNameLabel.setText(roomName);
        usersCountLabel.setText("conectando…");
        sendBtn.setDisable(true);

        msgInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !e.isShiftDown()) {
                e.consume();
                onSend();
            }
        });

        conectar();
    }

    private void conectar() {
        Thread t = new Thread(() -> {
            try {
                socket        = new Socket(ChatConfig.host(), port);
                clienteSocket = new ClienteSocket(socket);

                Mensagem entrar = new Mensagem();
                entrar.type    = TIPO_ENTRAR;
                entrar.texto   = roomName;
                entrar.usuario = currentEmail;
                entrar.email   = currentEmail;
                clienteSocket.EnviarMensagem(entrar);

                Platform.runLater(() -> {
                    usersCountLabel.setText("online");
                    sendBtn.setDisable(false);
                    appendSystem("Você entrou em " + roomName);
                });

                String msg;
                while ((msg = clienteSocket.ReceberMensagem()) != null) {
                    final String linha = msg;
                    Platform.runLater(() -> tratarLinha(linha));
                }

                if (!saindo) {
                    Platform.runLater(() -> {
                        appendSystem("Conexão encerrada (sala lotada ou servidor fora do ar).");
                        sendBtn.setDisable(true);
                        usersCountLabel.setText("desconectado");
                    });
                }
            } catch (Exception e) {
                if (!saindo) {
                    Platform.runLater(() -> {
                        appendSystem("Erro de conexão: " + e.getMessage());
                        sendBtn.setDisable(true);
                        usersCountLabel.setText("desconectado");
                    });
                }
            }
        }, "chat-receiver");
        t.setDaemon(true);
        t.start();
    }

    private void tratarLinha(String linha) {
        if (linha.startsWith("{")) {
            try {
                Mensagem m = GSON.fromJson(linha, Mensagem.class);
                if (m != null && TIPO_ERRO.equals(m.type)) {
                    appendSystem("Erro: " + m.texto);
                    return;
                }
            } catch (JsonSyntaxException ignored) {
            }
            appendSystem(linha);
            return;
        }

        int sep = linha.indexOf(": ");
        if (sep < 0) {
            appendSystem(linha);
            return;
        }

        String autor = linha.substring(0, sep).trim();
        String texto = linha.substring(sep + 2).trim();

        if (autor.equals(currentEmail)) appendSent(autor, texto);
        else                            appendReceived(autor, texto);
    }

    @FXML
    void onSend() {
        String text = msgInput.getText().trim().replace("\r", "").replace("\n", " ");
        if (text.isEmpty() || clienteSocket == null) return;
        try {
            Mensagem mensagem = new Mensagem();
            mensagem.type  = TIPO_MENSAGEM;
            mensagem.texto = text;
            clienteSocket.EnviarMensagem(mensagem);
        } catch (Exception e) {
            appendSystem("Erro ao enviar: " + e.getMessage());
        }
        msgInput.clear();
    }

    @FXML
    void onBack() {
        saindo = true;
        try {
            if (clienteSocket != null) {
                Mensagem sair = new Mensagem();
                sair.type = TIPO_SAIR;
                clienteSocket.EnviarMensagem(sair);
            }
            if (socket != null) socket.close();
        } catch (Exception ignored) {}
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
        HBox row = new HBox(buildBubble(author, text, false));
        row.setAlignment(Pos.CENTER_LEFT);
        messagesBox.getChildren().add(row);
        scrollToBottom();
    }

    void appendSent(String author, String text) {
        HBox row = new HBox(buildBubble(author, text, true));
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
