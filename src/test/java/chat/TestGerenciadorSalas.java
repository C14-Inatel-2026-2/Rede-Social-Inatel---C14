package chat;

import br.inatel.redesocial.servidor.GerenciadorSalas;
import br.inatel.redesocial.servidor.Sala;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import static org.junit.jupiter.api.Assertions.*;

@ResourceLock("server-static-state")
public class TestGerenciadorSalas {
    @Test
    public void retornarSalaAdicionada() {
        GerenciadorSalas.addSala(10, "salaAdicionada");

        assertNotNull(GerenciadorSalas.getSala("salaAdicionada"));
    }

    @Test
    public void retornaNullParaSalaInexistente() {
        assertNull(GerenciadorSalas.getSala("salaQueNaoExiste"));
    }

    @Test
    public void retornaSempreAMesmaInstancia() {
        GerenciadorSalas.addSala(10, "salaMesmaInstancia");
        Sala primeira = GerenciadorSalas.getSala("salaMesmaInstancia");
        Sala segunda = GerenciadorSalas.getSala("salaMesmaInstancia");
        assertSame(primeira, segunda);
    }
    @Test
    public void guardaSalasComNomesDiferentesSeparadamente() {
        GerenciadorSalas.addSala(5, "salaA");
        GerenciadorSalas.addSala(20, "salaB");
        Sala salaA = GerenciadorSalas.getSala("salaA");
        Sala salaB = GerenciadorSalas.getSala("salaB");
        assertNotNull(salaA);
        assertNotNull(salaB);
        assertNotSame(salaA, salaB);
    }
    @Test
    public void deveSubstituirSalaComMesmoNome() {
        GerenciadorSalas.addSala(5, "salaRepetida");
        Sala antiga = GerenciadorSalas.getSala("salaRepetida");
        GerenciadorSalas.addSala(15, "salaRepetida");
        Sala nova = GerenciadorSalas.getSala("salaRepetida");
        assertNotSame(antiga, nova);
    }
    @Test
    public void deveDiferenciarMaiusculasDeMinusculas() {
        GerenciadorSalas.addSala(10, "Geral");
        assertNull(GerenciadorSalas.getSala("geral"));
    }


    @Test
    public void existeSalaRetornaTrueParaSalaExistente() {
        GerenciadorSalas.addSala(10, "salaExiste");

        assertTrue(GerenciadorSalas.existeSala("salaExiste"));
    }

    @Test
    public void existeSalaRetornaFalseParaSalaInexistente() {
        assertFalse(GerenciadorSalas.existeSala("salaInexistente"));
    }

    @Test
    public void existeSalaRetornaFalseParaNomeNulo() {
        assertFalse(GerenciadorSalas.existeSala(null));
    }

    @Test
    public void salasJsonDeveRetornarDadosDasSalas() {
        GerenciadorSalas.addSala(10, "salaJson");

        String json = GerenciadorSalas.salasJson();

        assertTrue(json.contains("\"name\":\"salaJson\""));
        assertTrue(json.contains("\"capacity\":10"));
        assertTrue(json.contains("\"users\":"
                + GerenciadorSalas.getSala("salaJson").getQuantidadeUsuarios()));
    }

    @Test
    public void salasJsonDeveIncluirTodasAsSalas() {
        GerenciadorSalas.addSala(5, "salaJsonA");
        GerenciadorSalas.addSala(20, "salaJsonB");

        String json = GerenciadorSalas.salasJson();

        assertTrue(json.contains("\"name\":\"salaJsonA\""));
        assertTrue(json.contains("\"name\":\"salaJsonB\""));
    }

}