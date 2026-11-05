package br.inatel.redesocial.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class ServidorSocket {
    private PrintWriter escritor;
    private BufferedReader leitor;
    private static final Gson GSON = new Gson();

    public ServidorSocket(Socket socket, ServidorFactory servidorFactory) {
        try {
            escritor = servidorFactory.criarPrintWriter(socket);
            leitor = servidorFactory.criarBufferReader(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String leitura() throws IOException {
        return leitor.readLine();
    }

    public void enviar(Mensagem msg) {
        synchronized (escritor){
            escritor.println(GSON.toJson(msg));
        }
    }

    public void enviarTexto(Mensagem msg){
        synchronized(escritor){
            escritor.println(msg.usuario + ": " + msg.texto);
        }
    }

    public void enviar(Mensagem msg, PrintWriter escritorDestino){
        synchronized(escritorDestino){
            escritorDestino.println(msg.usuario + ": " + msg.texto);
        }
    }

    public Mensagem receber() throws IOException {
        String lido;
        while ((lido=leitura()) != null) {
            Mensagem msg;
            try {
                msg = GSON.fromJson(lido, Mensagem.class);
            }
            catch (JsonSyntaxException e){
                enviar(Mensagem.erro("JSON inválido"));
                continue;
            }
            if(msg == null || msg.type == null){
                enviar(Mensagem.erro("Mensagem vazia! "));
                continue;
            }
            else if("sair".equals(msg.type)){
                break;
            }
            else {
                return msg;
            }
        }
        return null;
    }

    public PrintWriter getEscritor() {return escritor;}
}