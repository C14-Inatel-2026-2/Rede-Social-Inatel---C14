package chatzone;

import javafx.application.Platform;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class JavaFxExtension implements BeforeAllCallback {

    private static boolean iniciado = false;

    @Override
    public synchronized void beforeAll(ExtensionContext context) {
        if (iniciado) return;
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException jaIniciado) {
        }
        iniciado = true;
    }
}