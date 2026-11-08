import com.sun.net.httpserver.HttpServer;
import br.inatel.redesocial.servidor.ChatServerMain;
import java.net.InetSocketAddress;

public class Main {

    private static final int PORTA_PADRAO = 8089;

    public static void main(String[] args) throws Exception {
        ChatServerMain.start();
        int porta = Integer.parseInt(
                System.getenv().getOrDefault("PORT", String.valueOf(PORTA_PADRAO)));

        HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);
        server.createContext("/", exchange -> {
            byte[] corpo = "Rede Social Inatel - no ar".getBytes();
            exchange.sendResponseHeaders(200, corpo.length);
            exchange.getResponseBody().write(corpo);
            exchange.close();
        });
        server.createContext("/rooms", exchange -> {
            byte[] corpo = br.inatel.redesocial.servidor.GerenciadorSalas.salasJson()
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, corpo.length);
            exchange.getResponseBody().write(corpo);
            exchange.close();
        });
        server.start();
    }
}
