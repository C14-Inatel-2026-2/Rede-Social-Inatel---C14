package br.inatel.redesocial.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public interface ServidorFactory {

    BufferedReader criarBufferReader(Socket socket) throws IOException;

    PrintWriter criarPrintWriter(Socket socket) throws IOException;

    ServidorSocket criarServidorSocket(Socket socket,ServidorFactory servidorFactory) throws IOException;

    ServerSocket criarServidor(int porta) throws IOException;

    ServidorHandler criarServidorHandler(Socket socket, Set<PrintWriter> escritores, ServidorFactory servidorFactory);

}