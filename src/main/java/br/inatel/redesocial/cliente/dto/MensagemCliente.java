package br.inatel.redesocial.cliente.dto;

import java.util.List;

public class MensagemCliente {
    public String type;
    public String email;
    public String sala;
    public String usuario;
    public String texto;
    public List<String> usuariosOnline;
    public long timestamp;
    public int valorInt;
    public static br.inatel.redesocial.servidor.Mensagem erro(String texto) {
        br.inatel.redesocial.servidor.Mensagem m = new br.inatel.redesocial.servidor.Mensagem();
        m.type = "erro";
        m.texto = texto;
        return m;
    }
}