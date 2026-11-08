package chatzone;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

public class LobbyController {

    @FXML private Label      profileEmailLabel;
    @FXML private StackPane  profileModal;
    @FXML private FlowPane   roomsGrid;

    private String currentEmail;

    // Dados demo — substituir por chamada HTTP
    private static final List<Room> DEMO_ROOMS = List.of(
        new Room(1, "Sala Geral",     5,  12, 3000),
        new Room(2, "Sala Jogos",    12,  12, 3001),
        new Room(3, "Sala Música",    3,  12, 3002),
        new Room(4, "Sala Estudos",   0,  12, 3003),
        new Room(5, "Sala Filmes",    8,  12, 3004),
        new Room(6, "Sala Aleatória", 1,  12, 3005)
    );

    @FXML
    public void initialize() {
        // Impede que clique dentro do modal feche o overlay
        profileModal.lookup(".modal");
    }

    public void init(String email) {
        currentEmail = email;
        profileEmailLabel.setText(email);
        loadRooms();
    }

    private void loadRooms() {
        roomsGrid.getChildren().clear();
        // TODO: buscar salas via HTTP; por enquanto usa demo
        for (Room room : DEMO_ROOMS) {
            roomsGrid.getChildren().add(buildRoomCard(room));
        }
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
            Main.showChat(room.name, room.id, room.port, currentEmail);
        } catch (Exception e) {
            System.out.println("[TOAST] Erro ao entrar na sala: " + e.getMessage());
        }
    }

    @FXML void onModalInnerClick(javafx.scene.input.MouseEvent e) { e.consume(); }
    @FXML void onUserBtn()    { profileModal.setVisible(true); }
    @FXML void onModalClose() { profileModal.setVisible(false); }
    @FXML void onLogout() {
        profileModal.setVisible(false);
        try { Main.showAuth(); } catch (Exception ignored) {}
    }

    record Room(int id, String name, int users, int capacity, int port) {}
}
