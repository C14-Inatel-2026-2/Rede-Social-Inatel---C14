package br.inatel.redesocial.cliente.comandos;

import br.inatel.redesocial.cliente.dto.Mensagem;
import br.inatel.redesocial.cliente.Cliente;
import br.inatel.redesocial.cliente.dto.MensagemFactory;

public final class IdentificarComandoChat {

    private IdentificarComandoChat() {}

    public static Mensagem identificarComando(
            String msg,
            Cliente cliente) {

        String[] partes = InterpretarComando.interpretar(msg);

        String comando = partes[0];
        String texto = partes[1];

        ExecutarComandoSala.executar(
                comando,
                texto,
                cliente
        );

        return MensagemFactory.criar(
                comando,
                texto,
                cliente
        );
    }
}