package br.inatel.redesocial.cliente.comandos;

import br.inatel.redesocial.cliente.Cliente;

public final class ExecutarComandoSala {

    private ExecutarComandoSala() {}

    public static void executar(
            String comando,
            String texto,
            Cliente cliente) {

        switch (comando) {

            case "*entrar_sala" -> {
                cliente.addSala(texto);
                cliente.setSalaAtiva(texto);
            }

            case "*mudar_sala" -> {
                if (cliente.getSala().contains(texto)) {
                    cliente.setSalaAtiva(texto);
                } else {
                    System.out.println(
                            "Você não está cadastrado nesta sala!"
                    );
                }
            }
        }
    }
}