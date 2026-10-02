package chatzone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("ChatZone");
        stage.setResizable(false);
        showAuth();
        stage.show();
    }

    public static void showAuth() throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("auth.fxml"));
        Scene scene = new Scene(loader.load(), 420, 520);
        scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void showLobby(String email) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("lobby.fxml"));
        Scene scene = new Scene(loader.load(), 860, 600);
        scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());
        LobbyController ctrl = loader.getController();
        ctrl.init(email);
        primaryStage.setScene(scene);
    }

    public static void showChat(String roomName, int roomId, int port, String email) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("chat.fxml"));
        Scene scene = new Scene(loader.load(), 720, 600);
        scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());
        ChatController ctrl = loader.getController();
        ctrl.init(roomName, roomId, port, email);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
