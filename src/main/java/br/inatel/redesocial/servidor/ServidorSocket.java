package br.inatel.redesocial.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class ServidorSocket
{
    private PrintWriter escritor;
    private BufferedReader leitor ;

    public ServidorSocket(Socket socket,ServidorFactory servidorFactory)
    {
        try {
            escritor = servidorFactory.criarPrintWriter(socket);
            leitor = servidorFactory.criarBufferReader(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public  String leitura() throws IOException {return leitor.readLine();}

    public void enviar(String usuario ,String mensagem, Set<PrintWriter> escritores ) {
        escritores.forEach(escritor->
        {
            escritor.println(usuario+" : "+mensagem);});
    }
}