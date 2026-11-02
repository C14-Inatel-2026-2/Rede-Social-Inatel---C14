package br.inatel.redesocial.servidor;

import java.io.*;
import java.net.Socket;
import java.util.Set;

public class ServidorHandler  {

    private Socket socket;
    private static Set<PrintWriter> escritores;
    private ServidorFactory servidorFactory;
    private String usuario;
    private ServidorSocket servidorSocket;

    public  ServidorHandler(Socket socket,Set<PrintWriter> escritores,ServidorFactory servidorFactory){
        this.socket = socket;
        this.escritores=escritores;
        this.servidorFactory = servidorFactory;
    }
    
}