package br.inatel.redesocial.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private int porta;
    public Servidor(int porta){
        if(porta<1 || porta>65535) throw new IllegalArgumentException();
        this.porta = porta;
    }
    public void ativarServidor() {
        try (ServerSocket socketServidor = new ServerSocket(this.porta);
             Socket conexao = socketServidor.accept();
        ) {

            DataInputStream entrada = new DataInputStream(conexao.getInputStream());
            DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
    }

}
