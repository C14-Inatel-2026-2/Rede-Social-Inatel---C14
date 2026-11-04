package br.inatel.redesocial.servidor;

import br.inatel.redesocial.autenticacao.GerenciadorUsuarios;
import br.inatel.redesocial.autenticacao.GerenciadorUsuariosMock;

import java.io.*;
import java.net.Socket;
import java.util.Set;

public class ServidorHandler extends Thread {

    private Socket socket;
    private static Set<PrintWriter> escritores;
    private ServidorFactory servidorFactory;
    private String emailUsuario;
    private String nomeSalaAtual;
    private Sala instanciaSala;
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
            emailUsuario = servidorSocket.leitura();
            nomeSalaAtual = servidorSocket.leitura();
            instanciaSala = GerenciadorSalas.getSala(nomeSalaAtual);
            if (instanciaSala==null){
                throw new IllegalArgumentException("Sala não encontrada");
            }
            else if(instanciaSala.isLotada()){
                throw new IllegalArgumentException("Sala lotada!");
            }
            GerenciadorUsuarios managerUsuarios = new GerenciadorUsuariosMock();
            instanciaSala.addUsuario(managerUsuarios.buscaUsuarioPorNome(emailUsuario));

            emailUsuario = emailUsuario.concat("\u0001").concat(nomeSalaAtual);

            servidorSocket.enviar(emailUsuario, "novo usuario : ", escritores, nomeSalaAtual);

            servidorSocket.receber(emailUsuario, escritores, nomeSalaAtual);
            
            synchronized (escritores) {
                escritores.remove(servidorSocket.getEscritor());
            }
            servidorSocket.enviar(emailUsuario, "saiu : ", escritores, nomeSalaAtual);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}