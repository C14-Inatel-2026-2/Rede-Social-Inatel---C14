package br.inatel.redesocial.servidor;

import br.inatel.redesocial.autenticacao.GerenciadorUsuarios;
import br.inatel.redesocial.autenticacao.GerenciadorUsuariosMock;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.net.Socket;
import java.util.Set;

public class ServidorHandler extends Thread {
    private static final Gson GSON = new Gson();
    private final Socket socket;

    private ServidorFactory servidorFactory;
    private String emailUsuario;
    private String nomeSala;
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
            instanciaSala.removeUsuario(emailUsuario, servidorSocket.getEscritor());
        }
        try { socket.close(); } catch (IOException ignored) {}
    }
    private void escolher(Mensagem msg){
        switch(msg.type){
            case "entrar_sala":
                if ((instanciaSala = GerenciadorSalas.getSala(msg.texto))==null){
                    servidorSocket.enviar(Mensagem.erro("Sala vazia!"));
                }
                else{
                    emailUsuario = msg.usuario;
                    instanciaSala.addUsuario(msg.usuario, servidorSocket.getEscritor());
                }

                break;
            case "criar_sala":
                GerenciadorSalas.addSala(msg.valorInt, msg.sala);
                break;
            case "mensagem":
                if (instanciaSala!=null){
                    msg.usuario = emailUsuario;
                    for (PrintWriter escritorDestino : instanciaSala.getEscritores()) {
                        servidorSocket.enviar(msg, escritorDestino);
                    }
                }
                else{
                    servidorSocket.enviar(Mensagem.erro("sala inexistente!"));
                }
                break;
            default:
                servidorSocket.enviar(Mensagem.erro("Opção não existente escolhida!"));
                break;
        }
    }
}