package br.inatel.redesocial.servidor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GerenciadorSalas {
    static private Map<String, Sala> salas = new HashMap<>();

    public static void addSala(int capacidadeMaxima, String nome){
        salas.put(nome, new Sala(capacidadeMaxima, nome));
    }
    public static Sala getSala(String nome){
        return salas.get(nome);
    }


}
