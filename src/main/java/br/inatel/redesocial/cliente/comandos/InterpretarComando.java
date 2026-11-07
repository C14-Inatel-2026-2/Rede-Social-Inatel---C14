package br.inatel.redesocial.cliente.comandos;

public final class InterpretarComando {

    private InterpretarComando() {}

    public static String[] interpretar(String msg) {

        if (msg != null && msg.contains("->")) {
            String[] partes = msg.split("->", -1);

            return new String[]{
                    partes[0].trim(),
                    partes[1].trim()
            };
        }

        return new String[]{"mensagem", msg != null ? msg.trim() : ""};
    }
}