package chatzone;

/** Runtime configuration for the desktop client and the chat server. */
public final class ChatConfig {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_HTTP_BASE_URL = "http://localhost:8089";
    private static final int DEFAULT_PORT_BASE = 3000;

    private ChatConfig() {
    }

    public static String host() {
        return envOrDefault("CHAT_SERVER_HOST", DEFAULT_HOST);
    }

    public static String httpBaseUrl() {
        return envOrDefault("CHAT_SERVER_HTTP_URL", DEFAULT_HTTP_BASE_URL);
    }

    public static int portForRoom(int roomId) {
        return parsePortBase(envOrDefault("CHAT_SERVER_PORT_BASE", String.valueOf(DEFAULT_PORT_BASE)))
                + roomId - 1;
    }

    private static String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static int parsePortBase(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return DEFAULT_PORT_BASE;
        }
    }
}
