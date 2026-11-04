package Chat;

import br.inatel.redesocial.servidor.GerenciadorSalas;
import br.inatel.redesocial.servidor.Sala;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}