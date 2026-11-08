package chatzone;

import br.inatel.redesocial.cliente.ClienteSocket;
import br.inatel.redesocial.cliente.dto.Mensagem;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.Socket;

public class LobbyController {

    @FXML private Label      profileEmailLabel;
    @FXML private StackPane  profileModal;
    @FXML private FlowPane   roomsGrid;

    private String currentEmail;

    private static final Gson GSON = new Gson();

    @FXML
    public void initialize() {
        // Impede que clique dentro do modal feche o overlay
        profileModal.lookup(".modal");
    }

    public void init(String email) {
        currentEmail = email;
        String nome = Session.nome();
        profileEmailLabel.setText(nome == null || nome.isBlank() ? email : nome + " (" + email + ")");
        loadRooms();
    }

    private void loadRooms() {
        roomsGrid.getChildren().clear();
        Thread loader = new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder(
                        URI.create(ChatConfig.httpBaseUrl() + "/rooms")).GET().build();
                HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
                Room[] rooms = GSON.fromJson(response.body(), Room[].class);
                Platform.runLater(() -> {
                    for (int i = 0; i < rooms.length; i++) {
                        Room room = rooms[i];
                        roomsGrid.getChildren().add(buildRoomCard(
                                new Room(i + 1, room.name, room.users, room.capacity, 0)));
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> roomsGrid.getChildren().clear());
            }
        }, "rooms-loader");
        loader.setDaemon(true);
        loader.start();
    }

    private VBox buildRoomCard(Room room) {
        boolean full = room.users >= room.capacity;
        double pct   = (double) room.users / room.capacity;

        VBox card = new VBox(8);
        card.getStyleClass().add("room-card");
        if (full) card.getStyleClass().add("room-card-full");
        card.setPrefWidth(180);

        Label name = new Label(room.name);
        name.getStyleClass().add("room-name");

        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label dot = new Label("●");
        dot.getStyleClass().add(full ? "dot-full" : "dot-online");
        Label count = new Label(room.users + "/" + room.capacity + " online");
        count.getStyleClass().add("room-meta");
        meta.getChildren().addAll(dot, count);

        StackPane barBg = new StackPane();
        barBg.getStyleClass().add("capacity-bar");
        Region fill = new Region();
        fill.getStyleClass().add(full ? "capacity-fill-full" : "capacity-fill");
        fill.setPrefWidth(pct * 160);
        fill.setPrefHeight(4);
        StackPane.setAlignment(fill, javafx.geometry.Pos.CENTER_LEFT);
        barBg.getChildren().add(fill);

        card.getChildren().addAll(name, meta, barBg);

        if (!full) {
            card.setOnMouseClicked(e -> enterRoom(room));
            card.getStyleClass().add("room-card-hover");
        }

        return card;
    }

    private void enterRoom(Room room) {
        // TODO: chamar API para entrar na sala e obter porta real
        try {
            Main.showChat(room.name, room.id, ChatConfig.portForRoom(room.id), currentEmail);
        } catch (Exception e) {
            System.out.println("[TOAST] Erro ao entrar na sala: " + e.getMessage());
        }
    }

    @FXML
    void onCreateRoom() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Criar sala");
        dialog.setHeaderText("Nome da nova sala");
        dialog.setContentText("Nome:");
        dialog.showAndWait().ifPresent(nome -> {
            String sala = nome.trim();
            if (sala.isBlank()) return;
            try (Socket socket = new Socket(ChatConfig.host(), ChatConfig.portForRoom(1));
                 ClienteSocket cliente = new ClienteSocket(socket)) {
                Mensagem criar = new Mensagem();
                criar.type = "criar_sala";
                criar.sala = sala;
                criar.valorInt = 12;
                cliente.EnviarMensagem(criar);
                loadRooms();
            } catch (Exception e) {
                System.out.println("[TOAST] Não foi possível criar a sala: " + e.getMessage());
            }
        });
    }

    @FXML void onModalInnerClick(javafx.scene.input.MouseEvent e) { e.consume(); }
    @FXML void onUserBtn()    { profileModal.setVisible(true); }
    @FXML void onModalClose() { profileModal.setVisible(false); }
    @FXML void onLogout() {
        Session.clear();
        profileModal.setVisible(false);
        try { Main.showAuth(); } catch (Exception ignored) {}
    }

    record Room(int id, String name, int users, int capacity, int port) {}
}
