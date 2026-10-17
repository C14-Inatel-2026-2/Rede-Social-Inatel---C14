module chatzone {
    requires javafx.controls;
    requires javafx.fxml;

    opens chatzone to javafx.fxml, org.mockito;
    exports chatzone;
}
