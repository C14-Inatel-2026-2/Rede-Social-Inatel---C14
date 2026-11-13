package chatzone;

import br.inatel.redesocial.cliente.ClienteFactory;
import br.inatel.redesocial.cliente.dto.MensagemCliente;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(JavaFxExtension.class)
public class ChatControllerTest {

    private static final String EMAIL = "user@email.com";
    private static final String OUTRO = "outro@email.com";

    @Mock
    private ClienteFactory clienteFactory;

    private ChatController controller;
    private Label usersCountLabel;
    private VBox messagesBox;
    private ScrollPane messagesScroll;
    private Button sendBtn;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        controller = new ChatController(clienteFactory);

        FXMLLoader loader = new FXMLLoader(ChatController.class.getResource("chat.fxml"));
        loader.setControllerFactory(tipo -> controller);
        loader.load();

        usersCountLabel = (Label) loader.getNamespace().get("usersCountLabel");
        messagesBox = (VBox) loader.getNamespace().get("messagesBox");
        messagesScroll = (ScrollPane) loader.getNamespace().get("messagesScroll");
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

    @Test
    public void testeConversaMantemOrdemDasMensagens() {
        controller.appendSystem("Você entrou em Sala Geral");
        controller.appendReceived(OUTRO, "Oi", "");
        controller.appendSent(EMAIL, "Olá", "");

        HBox ultima = (HBox) messagesBox.getChildren().get(2);

        assertEquals(3, messagesBox.getChildren().size());
        assertEquals(Pos.CENTER_RIGHT, ultima.getAlignment());
    }

    @Test
    public void testeMensagemRecebidaAlinhadaAEsquerda() {
        controller.appendReceived(OUTRO, "Oi", "");

        HBox linha = (HBox) messagesBox.getChildren().get(0);

        assertEquals(Pos.CENTER_LEFT, linha.getAlignment());
    }
    @Test
    public void testeBolhaRecebidaMostraAutorETexto() {
        controller.appendReceived("outro@email.com", "Oi","14:30");

        HBox linha = (HBox) messagesBox.getChildren().get(0);
        VBox bolha = (VBox) linha.getChildren().get(0);
        Label autor = (Label) bolha.getChildren().get(0);
        Label texto = (Label) bolha.getChildren().get(1);
        Label hora  = (Label) bolha.getChildren().get(2);

        assertEquals("outro@email.com", autor.getText());
        assertEquals("Oi", texto.getText());
        assertEquals("14:30", hora.getText());
    }

    @Test
    public void testeConversaEntreDoisUsuariosAlternaOsLados() {
        controller.appendReceived(OUTRO, "Oi, tudo bem?", "11:16");
        controller.appendSent(EMAIL, "Tudo sim!", "11:16");
        controller.appendReceived(OUTRO, "Que bom", "11:17");

        HBox primeira = (HBox) messagesBox.getChildren().get(0);
        HBox segunda  = (HBox) messagesBox.getChildren().get(1);
        HBox terceira = (HBox) messagesBox.getChildren().get(2);

        assertEquals(Pos.CENTER_LEFT, primeira.getAlignment());
        assertEquals(Pos.CENTER_RIGHT, segunda.getAlignment());
        assertEquals(Pos.CENTER_LEFT, terceira.getAlignment());
    }
    @Test
    public void testeConversaRolaParaUltimaMensagem() throws Exception {
        controller.appendReceived(OUTRO, "Oi","12:30");
        controller.appendSent(EMAIL, "Olá","12:30");
        CountDownLatch telaAtualizada = new CountDownLatch(1);
        Platform.runLater(telaAtualizada::countDown);

        telaAtualizada.await(5, TimeUnit.SECONDS);

        assertEquals(1.0, messagesScroll.getVvalue());
    }

    @Test
    public void testeAvisoDoSistemaNoMeioDaConversa() {
        controller.appendReceived(OUTRO, "Tchau","12:00");
        controller.appendSystem("Conexão encerrada");
        controller.appendReceived(EMAIL, "Alguém aí?","12:01");

        Label aviso = (Label) messagesBox.getChildren().get(1);

        assertEquals("Conexão encerrada", aviso.getText());
    }

    @Test
    public void testeErroDoServidorMostraAvisoNaConversa() {
        MensagemCliente erro = new MensagemCliente();
        erro.type = "erro";
        erro.texto = "Sala lotada!";

        controller.tratarMensagem(erro);

        Label aviso = (Label) messagesBox.getChildren().get(0);

        assertEquals("Erro: Sala lotada!", aviso.getText());
    }

    @Test
    public void testeErroDoServidorDesabilitaEnvio() {
        MensagemCliente confirmacao = new MensagemCliente();
        confirmacao.type = "entrada_confirmada";
        MensagemCliente erro = new MensagemCliente();
        erro.type = "erro";
        erro.texto = "Sala lotada!";
        controller.tratarMensagem(confirmacao);

        controller.tratarMensagem(erro);

        assertTrue(sendBtn.isDisabled());
    }
}
