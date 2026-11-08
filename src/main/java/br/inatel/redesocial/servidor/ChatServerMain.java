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

    public static void main(String[] args) {
        ServidorFactory factory = new ServidorFactoryImp();

        for (int i = 0; i < NOMES_SALAS.length; i++) {
            String nomeSala = NOMES_SALAS[i];
            int porta = PORTA_INICIAL + i;
            GerenciadorSalas.addSala(CAPACIDADE_SALA, nomeSala);

            Thread servidor = new Thread(
                    () -> new Servidor(porta, factory).servidorAtivo(),
                    "servidor-" + porta
            );
            servidor.start();
            System.out.printf("Sala %s disponível na porta %d%n", nomeSala, porta);
        }
    }
}
