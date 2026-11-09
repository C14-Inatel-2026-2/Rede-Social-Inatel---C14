module chatzone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;
    requires jdk.httpserver;
    requires com.google.gson;

    opens chatzone to javafx.fxml, org.mockito, com.google.gson;
    opens br.inatel.redesocial.cliente.dto to com.google.gson;
    opens br.inatel.redesocial.servidor to com.google.gson;
    exports chatzone;
}
