package chatzone;

/** Runtime configuration for the desktop client and the chat server. */
public final class ChatConfig {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT_BASE = 3000;

    private ChatConfig() {
    }

    public static String host() {
        String value = System.getenv("CHAT_SERVER_HOST");
        return value == null || value.isBlank() ? DEFAULT_HOST : value;
    }

    public static int portForRoom(int roomId) {
        String configured = System.getenv("CHAT_SERVER_PORT_BASE");
        int basePort = DEFAULT_PORT_BASE;
        if (configured != null && !configured.isBlank()) {
            try {
                basePort = Integer.parseInt(configured);
            } catch (NumberFormatException ignored) {
                // Keep the local default when the variable is invalid.
            }
        }
        return basePort + roomId - 1;
    }
}
