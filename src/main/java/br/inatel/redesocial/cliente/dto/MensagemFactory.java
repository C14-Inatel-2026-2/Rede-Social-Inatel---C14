package br.inatel.redesocial.cliente.dto;

import br.inatel.redesocial.cliente.Cliente;

public final class MensagemFactory {

    private MensagemFactory() {}

    public static Mensagem criar(
            String comando,
            String texto,
            Cliente cliente) {

        Mensagem mensagem = new Mensagem();

        mensagem.type = comando.startsWith("*")
                ? comando.substring(1)
                : comando;

        mensagem.texto = texto;
        mensagem.sala = cliente.getSalaAtiva();
        mensagem.usuario = cliente.getUsuario();
        mensagem.email = cliente.getEmail();

        return mensagem;
    }
}