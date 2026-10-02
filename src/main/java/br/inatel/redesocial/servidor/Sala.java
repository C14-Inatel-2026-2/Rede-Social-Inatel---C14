package br.inatel.redesocial.servidor;

import br.inatel.redesocial.autenticacao.Usuario;
import java.util.ArrayList;

public class Sala {
    private int capacidadeMaxima;
    private int quantidadeUsuarios;
    private static int quantidadeSalas;
    private ArrayList<Usuario> usuariosAtivos = new ArrayList<>();


    public Sala(int capacidadeMaxima){
        quantidadeUsuarios=0;
        quantidadeSalas+=1;
        this.capacidadeMaxima = capacidadeMaxima;
    }
    public void addUsuario(Usuario usuarioNovo){
        if(quantidadeUsuarios<capacidadeMaxima) {
            quantidadeUsuarios += 1;
        }
        usuariosAtivos.add(usuarioNovo);
    }
    public void removeUsuario(Usuario usuarioRemover){
        if(quantidadeUsuarios>0){
            quantidadeUsuarios-=1;
            usuariosAtivos.remove(usuarioRemover);
        }
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public int getQuantidadeUsuarios() {
        return quantidadeUsuarios;
    }

    public static int getQuantidadeSalas() {
        return quantidadeSalas;
    }

    public ArrayList<Usuario> getUsuariosAtivos() {
        return usuariosAtivos;
    }
}
