module chatzone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.httpserver;

    opens chatzone to javafx.fxml, org.mockito;
    exports chatzone;
}
