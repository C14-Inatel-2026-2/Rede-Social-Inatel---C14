import br.inatel.redesocial.servidor.Servidor;
import br.inatel.redesocial.servidor.ServidorFactory;
import br.inatel.redesocial.cliente.ClienteFactory;
import br.inatel.redesocial.cliente.ClienteHandler;

import br.inatel.redesocial.servidor.ServidorFactoryImp;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {

    private static final int PORTA_PADRAO = 8089;
    private static final int PORTA_CHAT = 5000;

    public static void main(String[] args) throws Exception {

        // Endpoint HTTP para verificar se a aplicação está no ar
        int portaHttp = Integer.parseInt(
                System.getenv().getOrDefault(
                        "PORT",
                        String.valueOf(PORTA_PADRAO)
                )
        );

        HttpServer httpServer = HttpServer.create(
                new InetSocketAddress(portaHttp), 0
        );

        httpServer.createContext("/", exchange -> {
            byte[] corpo = "Rede Social Inatel - no ar"
                    .getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type", "text/plain; charset=UTF-8"
            );

            exchange.sendResponseHeaders(200, corpo.length);

            try (var resposta = exchange.getResponseBody()) {
                resposta.write(corpo);
            }
        });

        httpServer.start();

        System.out.println(
                "HTTP ativo na porta " + portaHttp
        );

        // Inicialização do servidor de chat
        ServidorFactory servidorFactory = new ServidorFactoryImp();
        Servidor servidor = new Servidor(
                PORTA_CHAT, servidorFactory
        );

        Thread threadServidor = new Thread(
                servidor::servidorAtivo
        );

        threadServidor.setName("servidor-chat");
        threadServidor.start();

        System.out.println(
                "Servidor de chat iniciado na porta " + PORTA_CHAT
        );

        // Inicialização do cliente local
        ClienteFactory clienteFactory = new ClienteFactory();

        ClienteHandler clienteHandler = new ClienteHandler(
                PORTA_CHAT,
                "localhost",
                clienteFactory
        );

        clienteHandler.gerenciarChat();
    }
}