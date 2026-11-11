module chatzone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires jbcrypt;
    requires jdk.httpserver;

    opens chatzone to javafx.fxml, org.mockito;
    opens br.inatel.redesocial.cliente.dto to com.google.gson;
    opens br.inatel.redesocial.servidor to com.google.gson;

    exports chatzone;
}