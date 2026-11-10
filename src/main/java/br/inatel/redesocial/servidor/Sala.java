package br.inatel.redesocial.servidor;

import br.inatel.redesocial.banco.funcoes.UsuarioDAO;
import br.inatel.redesocial.usuario.GerenciadorUsuariosBD;
import br.inatel.redesocial.usuario.Usuario;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Sala {
    private int capacidadeMaxima;
    private int quantidadeUsuarios;
    private String nome;
    private static int quantidadeSalas;
    private ArrayList<Usuario> usuariosAtivos = new ArrayList<>();
    // Implementação antiga: private final Set<PrintWriter> escritores = ConcurrentHashMap.newKeySet();
    Map<String, PrintWriter> escritores = new HashMap<>();

    private GerenciadorUsuariosBD gerenciadorUsuarios = new GerenciadorUsuariosBD(new UsuarioDAO());

    public Sala(int capacidadeMaxima, String nome){
        this.nome = nome;
        quantidadeUsuarios=0;
        quantidadeSalas+=1;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public boolean addUsuario(Usuario usuarioNovo, PrintWriter escritor){
        if(quantidadeUsuarios<capacidadeMaxima) {
            usuariosAtivos.add(usuarioNovo);
            quantidadeUsuarios = usuariosAtivos.size();
            escritores.put(usuarioNovo.getEmail(), escritor);
            return true;
        }
        return false;
    }

    public boolean addUsuario(String email, PrintWriter escritor){
        return this.addUsuario(gerenciadorUsuarios.buscaUsuarioPorEmail(email), escritor);
    }

    public void removeUsuario(Usuario usuarioRemover){
        if(quantidadeUsuarios>0){
            if(usuariosAtivos.indexOf(usuarioRemover)>-1) {
                var escritor = getEscritor(usuarioRemover);
                if (escritor != null) {
                    escritor.close();
                }
                escritores.put(usuarioRemover.getEmail(), null);
                usuariosAtivos.remove(usuarioRemover);
                quantidadeUsuarios-=1;
            }
        }
    }

    public void removeUsuario(String usuarioRemover){
        removeUsuario(gerenciadorUsuarios.buscaUsuarioPorEmail(usuarioRemover));
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

    public PrintWriter getEscritor(String nomeUsuario){
        return escritores.get(nomeUsuario);
    }

    public PrintWriter getEscritor(Usuario usuarioGet){
        return escritores.get(usuarioGet.getEmail());
    }

    public Collection<PrintWriter> getEscritores(){
        return escritores.values();
    }

    public boolean isLotada(){
        return this.capacidadeMaxima == this.quantidadeUsuarios;
    }
}
