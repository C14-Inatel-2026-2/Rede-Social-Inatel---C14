package br.inatel.redesocial.cliente.comandos;

import br.inatel.redesocial.cliente.dto.MensagemCliente;
import br.inatel.redesocial.cliente.Cliente;

public final class IdentificarComandoChat
{
    private IdentificarComandoChat() {}

    public static MensagemCliente identificarComando(String msg, Cliente cliente)
    {
        String comando = "";
        String texto = "";

        if (msg != null && msg.contains("->")) {
            String[] partes = msg.split("->", -1);

            comando = partes[0].trim();
            texto = partes[1].trim();
        }
        else {
            comando = "mensagem";
            texto = (msg != null) ? msg.trim() : "";
        }

        MensagemCliente mensagemCliente = new MensagemCliente();

        switch (comando) {
            case "*entrar_sala" ->
            {
                cliente.addSala(texto);
                cliente.setSalaAtiva(texto);
            }
            case "*mudar_sala" ->
            {
                if (cliente.getSala().contains(texto)) {
                    cliente.setSalaAtiva(texto);
                } else {
                        System.out.println("Você não está cadastrado nesta sala!");
                }
            }
            default ->
            {

            }
        }

        mensagemCliente.type = comando.startsWith("*") ? comando.substring(1) : comando;
        mensagemCliente.texto = texto;
        mensagemCliente.sala = cliente.getSalaAtiva();
        mensagemCliente.usuario = cliente.getUsuario();
        mensagemCliente.email = cliente.getEmail();

        return mensagemCliente;
    }
}
