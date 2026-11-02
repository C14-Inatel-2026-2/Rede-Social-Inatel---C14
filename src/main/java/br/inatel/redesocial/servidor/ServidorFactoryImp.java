package br.inatel.redesocial.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class ServidorFactoryImp implements ServidorFactory {

    @Override
    public BufferedReader criarBufferReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public PrintWriter criarPrintWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

}
