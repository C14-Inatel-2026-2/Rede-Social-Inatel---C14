package br.inatel.redesocial.servidor;

import br.inatel.redesocial.usuario.GerenciadorUsuariosBD;
import br.inatel.redesocial.usuario.Usuario;
import br.inatel.redesocial.banco.funcoes.UsuarioDAO;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sala {
    private int capacidadeMaxima;
    private int quantidadeUsuarios;
    private String nome;
    private static int quantidadeSalas;

    private ArrayList<Usuario> usuariosAtivos = new ArrayList<>();
    private GerenciadorUsuariosBD gerenciadorUsuarios =
            new GerenciadorUsuariosBD(new UsuarioDAO());

    private final Map<String, PrintWriter> escritores = new HashMap<>();

    public Sala(int capacidadeMaxima, String nome) {
        this.nome = nome;
        quantidadeUsuarios = 0;
        quantidadeSalas += 1;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public synchronized boolean addUsuario(Usuario usuarioNovo, PrintWriter escritor) {
        if (usuarioNovo != null && quantidadeUsuarios < capacidadeMaxima) {
            usuariosAtivos.add(usuarioNovo);
            quantidadeUsuarios = usuariosAtivos.size();
            escritores.put(usuarioNovo.getEmail(), escritor);
            return true;
        }
        return false;
    }

    public boolean addUsuario(String emailUsuario, PrintWriter escritor) {
        return this.addUsuario(
                gerenciadorUsuarios.buscaUsuarioPorEmail(emailUsuario), escritor
        );
    }

    public synchronized void removeUsuario(Usuario usuarioRemover) {
        if (usuarioRemover != null && usuariosAtivos.contains(usuarioRemover)) {
            PrintWriter escritor = getEscritor(usuarioRemover);

            if (escritor != null) {
                escritor.close();
            }

            escritores.remove(usuarioRemover.getEmail());
            usuariosAtivos.remove(usuarioRemover);
            quantidadeUsuarios = usuariosAtivos.size();
        }
    }

    public synchronized void removeUsuario(String emailUsuario) {
        if (emailUsuario == null) {
            return;
        }

        Usuario usuarioRemover = null;

        for (Usuario ativo : usuariosAtivos) {
            if (emailUsuario.equals(ativo.getEmail())) {
                usuarioRemover = ativo;
                break;
            }
        }

        removeUsuario(usuarioRemover);
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public String getNome() {
        return nome;
    }

    public synchronized int getQuantidadeUsuarios() {
        return quantidadeUsuarios;
    }

    public static int getQuantidadeSalas() {
        return quantidadeSalas;
    }

    public synchronized ArrayList<Usuario> getUsuariosAtivos() {
        return usuariosAtivos;
    }

    public synchronized PrintWriter getEscritor(String emailUsuario) {
        return escritores.get(emailUsuario);
    }

    public synchronized PrintWriter getEscritor(Usuario usuarioGet) {
        if (usuarioGet == null) {
            return null;
        }
        return escritores.get(usuarioGet.getEmail());
    }

    public synchronized Collection<PrintWriter> getEscritores() {
        return escritores.values();
    }

    public synchronized List<String> getNomesUsuariosAtivos() {
        return usuariosAtivos.stream()
                .map(Usuario::getEmail)
                .collect(Collectors.toList());
    }

    public synchronized boolean isLotada() {
        return this.capacidadeMaxima == this.quantidadeUsuarios;
    }
}
