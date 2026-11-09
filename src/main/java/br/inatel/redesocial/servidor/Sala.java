package br.inatel.redesocial.servidor;

import br.inatel.redesocial.autenticacao.GerenciadorUsuarios;
import br.inatel.redesocial.autenticacao.GerenciadorUsuariosMock;
import br.inatel.redesocial.autenticacao.Usuario;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

public class Sala {
    private int capacidadeMaxima;
    private int quantidadeUsuarios;
    private String nome;
    private static int quantidadeSalas;
    private ArrayList<Usuario> usuariosAtivos = new ArrayList<>();
    private GerenciadorUsuarios gerenciadorUsuarios = new GerenciadorUsuariosMock();
    private final Set<PrintWriter> escritores = ConcurrentHashMap.newKeySet();

    public Sala(int capacidadeMaxima, String nome){
        this.nome = nome;
        quantidadeUsuarios=0;
        quantidadeSalas+=1;
        this.capacidadeMaxima = capacidadeMaxima;
    }
    public synchronized boolean addUsuario(Usuario usuarioNovo, PrintWriter escritor){
        if(quantidadeUsuarios<capacidadeMaxima) {
            quantidadeUsuarios += 1;
            usuariosAtivos.add(usuarioNovo);
            escritores.add(escritor);
            return true;
        }
        return false;
    }

    public boolean addUsuario(String nomeUsuario, PrintWriter escritor){
        return this.addUsuario(gerenciadorUsuarios.buscaUsuarioPorNome(nomeUsuario), escritor);
    }

    public synchronized void removeUsuario(Usuario usuarioRemover, PrintWriter escritorRemover){
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

    public void removeUsuario(String usuarioRemover, PrintWriter escritorRemover){
        removeUsuario(gerenciadorUsuarios.buscaUsuarioPorNome(usuarioRemover), escritorRemover);
    }


    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public String getNome() {
        return nome;
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

    public synchronized List<String> getNomesUsuariosAtivos() {
        return usuariosAtivos.stream()
                .map(Usuario::getEmail)
                .collect(Collectors.toList());
    }

    public boolean isLotada(){
        return this.capacidadeMaxima == this.quantidadeUsuarios;
    }
}
