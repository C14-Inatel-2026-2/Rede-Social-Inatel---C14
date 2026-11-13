package chatzone;

import br.inatel.redesocial.cliente.*;
import br.inatel.redesocial.cliente.dto.MensagemCliente;
import br.inatel.redesocial.cliente.dto.MensagemFactory;
import br.inatel.redesocial.cliente.comandos.ExecutarComandoSala;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.util.Collections;
import java.util.List;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ChatController {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());
    private static final String TIPO_ENTRAR = "entrar_sala";
    private static final String TIPO_MENSAGEM = "mensagem";
    private static final String TIPO_SAIR = "sair";
    private static final String TIPO_ERRO = "erro";
    private static final String TIPO_PRESENCA = "presenca";
    private static final String TIPO_ENTRADA_CONFIRMADA = "entrada_confirmada";

    @FXML private Label roomNameLabel, usersCountLabel;
    @FXML private VBox messagesBox;
    @FXML private ScrollPane messagesScroll;
    @FXML private TextArea msgInput;
    @FXML private Button sendBtn;

    private String currentEmail;
    private String roomName;
    private Cliente cliente;
    private int roomId;
    private int port;
    private ClienteSocket clienteSocket;
    private volatile boolean saindo = false;

    private final ClienteFactory clienteFactory;

    public ChatController() {
        this(new ClienteFactory());
    }

    ChatController(ClienteFactory clienteFactory) {
        this.clienteFactory = clienteFactory;
    }

    public void init(String roomName, int roomId, int port, String email) {
        this.roomName = roomName;
        this.roomId = roomId;
        this.port = port;
        this.currentEmail = email;
        roomNameLabel.setText(roomName);
        usersCountLabel.setText("conectando...");
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
                ClienteHandler clienteHandler = new ClienteHandler(port, ChatConfig.host(), clienteFactory);
                Cliente conectado = clienteHandler.conectar(currentEmail, currentEmail);
                clienteSocket = conectado.getClienteSocket();
                cliente = conectado;
                ExecutarComandoSala.executar("*" + TIPO_ENTRAR, roomName, cliente);
                cliente.setOuvinte(mensagem -> Platform.runLater(() -> tratarMensagem(mensagem)));
                clienteSocket.EnviarMensagem(MensagemFactory.criar(TIPO_ENTRAR, roomName, cliente));
                cliente.receberMensagem();
                if (!saindo) mostrarDesconexao("Conexao encerrada pelo servidor.");
            } catch (Exception e) {
                if (!saindo) mostrarDesconexao("Erro de conexao: " + e.getMessage());
            }
        }, "chat-receiver");
        t.setDaemon(true);
        t.start();
    }

    void tratarMensagem(MensagemCliente mensagem) {
        if (mensagem == null) return;
        if (TIPO_ERRO.equals(mensagem.type)) {
            appendSystem("Erro: " + mensagem.texto);
            sendBtn.setDisable(true);
            return;
        }
        if (TIPO_ENTRADA_CONFIRMADA.equals(mensagem.type)) {
            sendBtn.setDisable(false);
            atualizarPresenca(mensagem);
            appendSystem("Entrada autorizada em " + roomName + ".");
            return;
        }
        if (TIPO_PRESENCA.equals(mensagem.type)) {
            atualizarPresenca(mensagem);
            return;
        }
        if (!TIPO_MENSAGEM.equals(mensagem.type)) {
            appendSystem(mensagem.texto == null ? mensagem.type : mensagem.texto);
            return;
        }
        String autor = mensagem.usuario == null ? "desconhecido" : mensagem.usuario;
        String texto = mensagem.texto == null ? "" : mensagem.texto;
        String hora = mensagem.timestamp > 0 ? HORA.format(Instant.ofEpochMilli(mensagem.timestamp)) : "";
        if (autor.equals(currentEmail)) appendSent(autor, texto, hora);
        else appendReceived(autor, texto, hora);
    }

    private void atualizarPresenca(MensagemCliente mensagem) {
        List<String> usuarios = mensagem.usuariosOnline == null
                ? Collections.emptyList() : mensagem.usuariosOnline;
        String nomes = usuarios.isEmpty() ? "nenhum" : String.join(", ", usuarios);
        usersCountLabel.setText(mensagem.valorInt + " online: " + nomes);
    }

    private void mostrarDesconexao(String texto) {
        Platform.runLater(() -> {
            appendSystem(texto);
            sendBtn.setDisable(true);
            usersCountLabel.setText("desconectado");
        });
    }

    @FXML void onSend() {
        String text = msgInput.getText().trim().replace("\r", "").replace("\n", " ");
        if (text.isEmpty() || cliente == null) return;
        try {
            clienteSocket.EnviarMensagem(MensagemFactory.criar(TIPO_MENSAGEM, text, cliente));
            msgInput.clear();
        } catch (Exception e) {
            appendSystem("Nao foi possivel enviar a mensagem. Verifique a conexao.");
        }
    }

    @FXML void onBack() {
        saindo = true;
        try {
            if (cliente != null) {
                clienteSocket.EnviarMensagem(MensagemFactory.criar(TIPO_SAIR, "", cliente));
            }
            if (clienteSocket != null) clienteSocket.close();
        } catch (Exception ignored) { }
        try { Main.showLobby(currentEmail); } catch (Exception ignored) { }
    }

    void appendSystem(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("msg-system");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        messagesBox.getChildren().add(label);
        scrollToBottom();
    }

    void appendReceived(String author, String text, String hora) {
        HBox row = new HBox(buildBubble(author, text, hora, false));
        row.setAlignment(Pos.CENTER_LEFT);
        messagesBox.getChildren().add(row);
        scrollToBottom();
    }

    void appendSent(String author, String text, String hora) {
        HBox row = new HBox(buildBubble(author, text, hora, true));
        row.setAlignment(Pos.CENTER_RIGHT);
        messagesBox.getChildren().add(row);
        scrollToBottom();
    }

    private VBox buildBubble(String author, String text, String hora, boolean sent) {
        Label authorLabel = new Label(author);
        authorLabel.getStyleClass().add(sent ? "msg-author-sent" : "msg-author");
        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(480);
        textLabel.getStyleClass().add("msg-text");
        Label timeLabel = new Label(hora);
        timeLabel.getStyleClass().add("msg-time");
        VBox bubble = new VBox(3, authorLabel, textLabel, timeLabel);
        bubble.getStyleClass().add(sent ? "msg-sent" : "msg-received");
        bubble.setMaxWidth(520);
        return bubble;
    }

    private void scrollToBottom() {
        Platform.runLater(() -> messagesScroll.setVvalue(1.0));
    }
}
