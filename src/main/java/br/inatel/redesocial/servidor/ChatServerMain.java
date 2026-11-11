package br.inatel.redesocial.servidor;

public class ChatServerMain {

    private static final String[] NOMES_SALAS = {
            "Sala Geral",
            "Sala Jogos",
            "Sala Música",
            "Sala Estudos",
            "Sala Filmes",
            "Sala Aleatória"
    };

    private static final int PORTA_INICIAL = Integer.parseInt(
            System.getenv().getOrDefault("CHAT_SERVER_PORT_BASE", "3000"));
    private static final int CAPACIDADE_SALA = Integer.parseInt(
            System.getenv().getOrDefault("CHAT_ROOM_CAPACITY", "12"));

    public static void start() {
        for (int i = 0; i < NOMES_SALAS.length; i++) {
            String nomeSala = NOMES_SALAS[i];
            int porta = PORTA_INICIAL + i;
            if (!GerenciadorSalas.existeSala(nomeSala)) {
                GerenciadorSalas.addSala(CAPACIDADE_SALA, nomeSala);
            }

            Thread servidor = new Thread(
                    () -> new Servidor(porta, new ServidorFactoryImp()).servidorAtivo(),
                    "servidor-" + porta
            );
            servidor.setDaemon(true);
            servidor.start();
        }
    }

    public static void main(String[] args) {
        start();
    }
}
