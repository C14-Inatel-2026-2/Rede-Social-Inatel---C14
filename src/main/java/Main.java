import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class Main {

    private static final int PORTA_PADRAO = 8089;

    public static void main(String[] args) throws Exception {
        int porta = Integer.parseInt(
                System.getenv().getOrDefault("PORT", String.valueOf(PORTA_PADRAO)));

        HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);
        server.createContext("/", exchange -> {
            byte[] corpo = "Rede Social Inatel - no ar".getBytes();
            exchange.sendResponseHeaders(200, corpo.length);
            exchange.getResponseBody().write(corpo);
            exchange.close();
        });
        server.start();
    }
}