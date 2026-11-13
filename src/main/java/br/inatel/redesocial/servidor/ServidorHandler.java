package br.inatel.redesocial.servidor;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.net.Socket;
import java.util.Set;
import java.util.ArrayList;

public class ServidorHandler extends Thread {
    private static final Gson GSON = new Gson();
    private final Socket socket;

    private ServidorFactory servidorFactory;
    private String emailUsuario;
    private Sala instanciaSala;

    private PrintWriter saida;
    private BufferedReader entrada;

    private ServidorSocket servidorSocket;

    public ServidorHandler(Socket socket, Set<PrintWriter> escritores, ServidorFactory servidorFactory) {
        this.socket = socket;
        this.servidorFactory = servidorFactory;
    }

    @Override
    public void run() {
        executar();
    }

    private void executar() {
        try {
            servidorSocket = servidorFactory.criarServidorSocket(socket, servidorFactory);
            Mensagem msg;
            while (true) {
                try {
                    msg = servidorSocket.receber();
                } catch (JsonSyntaxException e) {
                    servidorSocket.enviar(Mensagem.erro("JSON inválido"));
                    continue;
                }
                if (msg == null) break;
                if (msg.type == null) {
                    servidorSocket.enviar(Mensagem.erro("campo 'type' ausente"));
                    continue;
                }
                escolher(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            limpar();
        }
    }

    private void limpar() {
        if (instanciaSala != null) {
            instanciaSala.removeUsuario(emailUsuario);
            notificarSala(emailUsuario + " saiu da sala.");
            notificarPresenca();
        }
        try { socket.close(); } catch (IOException ignored) {}
    }
    private void escolher(Mensagem msg){
        switch(msg.type){
            case "entrar_sala":
                entrarNaSala(msg);
                break;
            case "criar_sala":
                criarSala(msg);
                break;
            case "mensagem":
                enviarMensagem(msg);
                break;
            default:
                servidorSocket.enviar(Mensagem.erro("Opção não existente escolhida!"));
                break;
        }
    }

    private void entrarNaSala(Mensagem msg) {
        instanciaSala = GerenciadorSalas.getSala(msg.texto);
        if (instanciaSala == null) {
            servidorSocket.enviar(Mensagem.erro("Sala inexistente!"));
            return;
        }

        emailUsuario = msg.usuario;
        if (!instanciaSala.addUsuario(msg.usuario, servidorSocket.getEscritor())) {
            servidorSocket.enviar(Mensagem.erro("Sala lotada!"));
            instanciaSala = null;
            return;
        }

        Mensagem confirmado = Mensagem.info("Entrada autorizada.");
        confirmado.type = "entrada_confirmada";
        confirmado.valorInt = instanciaSala.getQuantidadeUsuarios();
        confirmado.usuariosOnline = instanciaSala.getNomesUsuariosAtivos();
        servidorSocket.enviar(confirmado);
        notificarSala(msg.usuario + " entrou na sala.");
        notificarPresenca();
    }

    private void criarSala(Mensagem msg) {
        if (GerenciadorSalas.existeSala(msg.sala)) {
            servidorSocket.enviar(Mensagem.erro("Sala já existe!"));
            return;
        }
        if (msg.sala == null || msg.sala.isBlank() || msg.valorInt < 1) {
            servidorSocket.enviar(Mensagem.erro("Nome e capacidade inválidos!"));
            return;
        }

        GerenciadorSalas.addSala(msg.valorInt, msg.sala);
        servidorSocket.enviar(Mensagem.info("Sala criada!"));
    }

    private void enviarMensagem(Mensagem msg) {
        if (msg.texto == null) {
            servidorSocket.enviar(Mensagem.erro("Texto da mensagem ausente."));
            return;
        }
        if (instanciaSala == null) {
            servidorSocket.enviar(Mensagem.erro("sala inexistente!"));
            return;
        }

        msg.usuario = emailUsuario;
        msg.timestamp = System.currentTimeMillis();
        for (PrintWriter escritorDestino : instanciaSala.getEscritores()) {
            servidorSocket.enviar(msg, escritorDestino);
        }
    }

    private void notificarSala(String texto) {
        if (instanciaSala == null) return;
        Mensagem aviso = Mensagem.info(texto);
        for (PrintWriter destino : instanciaSala.getEscritores()) {
            servidorSocket.enviar(aviso, destino);
        }
    }

    private void notificarPresenca() {
        if (instanciaSala == null) return;
        Mensagem presenca = new Mensagem();
        presenca.type = "presenca";
        presenca.valorInt = instanciaSala.getQuantidadeUsuarios();
        presenca.usuariosOnline = new ArrayList<>(instanciaSala.getNomesUsuariosAtivos());
        for (PrintWriter destino : instanciaSala.getEscritores()) {
            servidorSocket.enviar(presenca, destino);
        }
    }
}
