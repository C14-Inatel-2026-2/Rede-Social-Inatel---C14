package br.inatel.redesocial.cliente.comandos;

import br.inatel.redesocial.cliente.dto.Mensagem;

public final class  IdentificarComandoChat
{

    private IdentificarComandoChat() {}

    public static Mensagem identificarComando(String msg, String usuario, String email, String sala)
    {
        String comando = "";
        String texto = "";

        if (msg != null && msg.contains("->")) {
            String[] partes = msg.split("->", -1);

            comando = partes[0].trim();
            texto = partes[1].trim();
            sala = texto ;
        }
        else {
            comando = "mensagem";
            texto = (msg != null) ? msg.trim() : "";
        }

        Mensagem mensagem = new Mensagem();

        switch (comando) {
            case "*entrar_sala" -> {
                mensagem.type = "entrar_sala";
                mensagem.texto = texto;
            }
            default -> {
                mensagem.type = comando;
                mensagem.texto = texto;
            }
        }

        mensagem.usuario=usuario;
        mensagem.email=email;

        return mensagem;
    }
}