package br.inatel.redesocial.servidor;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

public class GerenciadorSalas {
    static private Map<String, Sala> salas = new HashMap<>();

    public static void addSala(int capacidadeMaxima, String nome){
        salas.put(nome, new Sala(capacidadeMaxima, nome));
    }
    public static Sala getSala(String nome){
        return salas.get(nome);
    }

    public static boolean existeSala(String nome) {
        return nome != null && salas.containsKey(nome);
    }

    public static String salasJson() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Sala sala : salas.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", sala.getNome());
            item.put("users", sala.getQuantidadeUsuarios());
            item.put("capacity", sala.getCapacidadeMaxima());
            resultado.add(item);
        }
        return new Gson().toJson(resultado);
    }
}
