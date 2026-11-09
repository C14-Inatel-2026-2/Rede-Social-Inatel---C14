package br.inatel.redesocial.servidor;

import br.inatel.redesocial.banco.funcoes.UsuarioDAO;
import br.inatel.redesocial.usuario.GerenciadorUsuariosBD;
import br.inatel.redesocial.usuario.Usuario;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Sala {
    private int capacidadeMaxima;
    private int quantidadeUsuarios;
    private String nome;
    private static int quantidadeSalas;
    private ArrayList<Usuario> usuariosAtivos = new ArrayList<>();
    private GerenciadorUsuariosBD gerenciadorUsuarios = new GerenciadorUsuariosBD(new UsuarioDAO());
    private final Set<PrintWriter> escritores = ConcurrentHashMap.newKeySet();

    public Sala(int capacidadeMaxima, String nome){
        this.nome = nome;
        quantidadeUsuarios=0;
        quantidadeSalas+=1;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public boolean addUsuario(Usuario usuarioNovo, PrintWriter escritor){
        if(quantidadeUsuarios<capacidadeMaxima) {
            quantidadeUsuarios += 1;
            usuariosAtivos.add(usuarioNovo);
            escritores.add(escritor);
            return true;
        }
        return false;
    }

    public boolean addUsuario(String email, PrintWriter escritor){
        return this.addUsuario(gerenciadorUsuarios.buscaUsuarioPorEmail(email), escritor);
    }

    public void removeUsuario(Usuario usuarioRemover, PrintWriter escritorRemover){
        if(quantidadeUsuarios>0){
            if(usuariosAtivos.indexOf(usuarioRemover)>-1) {
                usuariosAtivos.remove(usuarioRemover);
                quantidadeUsuarios-=1;
            }
        }
        if(escritores.size()>0){
            escritores.remove(escritorRemover);
        }
    }

    public void removeUsuario(String emailUsuarioRemover, PrintWriter escritorRemover){
        removeUsuario(gerenciadorUsuarios.buscaUsuarioPorEmail(emailUsuarioRemover), escritorRemover);
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

    public Set<PrintWriter> getEscritores(){
        return escritores;
    }

    public boolean isLotada(){
        return this.capacidadeMaxima == this.quantidadeUsuarios;
    }
}
