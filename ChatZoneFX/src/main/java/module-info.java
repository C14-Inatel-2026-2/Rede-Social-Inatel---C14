module chatzone {
    requires javafx.controls;
    requires javafx.fxml;

    opens chatzone to javafx.fxml;
    exports chatzone;
}
