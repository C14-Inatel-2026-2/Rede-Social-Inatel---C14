package br.inatel.redesocial.servidor;

import java.util.List;

public class Mensagem {
    public String type;
    public String email;
    public String sala;
    public String usuario;
    public String texto;
    public int valorInt;
    public List<String> usuariosOnline;
    public static Mensagem erro(String texto) {
        Mensagem m = new Mensagem();
        m.type = "erro";
        m.texto = texto;
        return m;
    }

    public static Mensagem info(String texto) {
        Mensagem m = new Mensagem();
        m.type = "sistema";
        m.texto = texto;
        return m;
    }
}
