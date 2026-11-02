package br.inatel.redesocial.servidor;

import java.io.*;
import java.net.Socket;
import java.util.Set;

public class ServidorHandler extends Thread {

    private Socket socket;
    private static Set<PrintWriter> escritores;
    private ServidorFactory servidorFactory;
    private String usuario;
    private String salaAtual;
    private ServidorSocket servidorSocket;

    public ServidorHandler(Socket socket, Set<PrintWriter> escritores, ServidorFactory servidorFactory) {
        this.socket = socket;
        this.escritores = escritores;
        this.servidorFactory = servidorFactory;
    }

    @Override
    public void run() {
        executar();
    }

    private void executar() {
        try {
            servidorSocket = servidorFactory.criarServidorSocket(socket, servidorFactory);

            synchronized (escritores) {
                escritores.add(servidorSocket.getEscritor());
            }
            usuario = servidorSocket.leitura();
            salaAtual = servidorSocket.leitura();
            if (GerenciadorSalas.getSala(salaAtual)==null){
                throw new IllegalArgumentException("Sala não encontrada");
            }
            if(GerenciadorSalas.getSala(salaAtual).isLotada()){
                throw new IllegalArgumentException("Sala lotada!");
            }
            //todo adicionar usuario às salas do gerenciador de salas (usuario usa email e senha, mas aqui é nome)
            usuario = usuario.concat("\u0001").concat(salaAtual);

            servidorSocket.enviar(usuario, "novo usuario : ", escritores, salaAtual);

            servidorSocket.receber(usuario, escritores, salaAtual);

            synchronized (escritores) {
                escritores.remove(servidorSocket.getEscritor());
            }
            servidorSocket.enviar(usuario, "saiu : ", escritores, salaAtual);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}