package co.edu.javeriana.fis.electoral.patrones.creacional.builder;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.dominio.Acta;
import co.edu.javeriana.fis.electoral.dominio.Candidato;
import co.edu.javeriana.fis.electoral.dominio.Mesa;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ActaBuilder: construccion validada del acta")
class ActaBuilderTest {

    @Test
    @DisplayName("construir: crea el acta con mesa, fecha y resultados")
    void construirValido() {
        Mesa mesa = new Mesa(1, 100);
        Candidato candidato = Fixtures.candidato("1");
        Acta acta = new ActaBuilder()
                .conMesa(mesa)
                .conFechaCierre(Fixtures.FECHA_CIERRE)
                .agregarResultado(candidato, 10)
                .construir();
        assertEquals(mesa, acta.getMesa());
        assertEquals(Fixtures.FECHA_CIERRE, acta.getFechaCierre());
        assertEquals(10, acta.votosDe(candidato));
    }

    @Test
    @DisplayName("construir: permite un acta sin resultados (caso limite)")
    void construirSinResultados() {
        Acta acta = new ActaBuilder().conMesa(new Mesa(1, 100)).construir();
        assertEquals(0, acta.totalVotos());
    }

    @Test
    @DisplayName("construir: asigna fecha actual si no se especifica")
    void construirSinFechaUsaAhora() {
        Acta acta = new ActaBuilder().conMesa(new Mesa(1, 100)).construir();
        assertNotNull(acta.getFechaCierre());
    }

    @Test
    @DisplayName("construir: falla si no se definio la mesa")
    void construirSinMesaFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> new ActaBuilder().agregarResultado(Fixtures.candidato("1"), 10).construir());
    }

    @Test
    @DisplayName("conMesa: rechaza mesa nula")
    void conMesaNulaFalla() {
        assertThrows(IllegalArgumentException.class, () -> new ActaBuilder().conMesa(null));
    }

    @Test
    @DisplayName("conFechaCierre: rechaza fecha nula")
    void conFechaNulaFalla() {
        assertThrows(IllegalArgumentException.class, () -> new ActaBuilder().conFechaCierre(null));
    }

    @Test
    @DisplayName("agregarResultado: rechaza candidato nulo")
    void agregarCandidatoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> new ActaBuilder().agregarResultado(null, 10));
    }

    @Test
    @DisplayName("agregarResultado: rechaza votos negativos (caso limite)")
    void agregarVotosNegativosFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> new ActaBuilder().agregarResultado(Fixtures.candidato("1"), -5));
    }

    @Test
    @DisplayName("agregarResultado: rechaza candidato duplicado")
    void agregarDuplicadoFalla() {
        Candidato candidato = Fixtures.candidato("1");
        ActaBuilder builder = new ActaBuilder().agregarResultado(candidato, 10);
        assertThrows(ValidacionElectoralException.class,
                () -> builder.agregarResultado(candidato, 5));
    }

    @Test
    @DisplayName("construir: la fecha indicada se conserva")
    void fechaPersonalizada() {
        LocalDateTime fecha = LocalDateTime.of(2026, 3, 8, 16, 45);
        Acta acta = new ActaBuilder()
                .conMesa(new Mesa(1, 100))
                .conFechaCierre(fecha)
                .construir();
        assertEquals(fecha, acta.getFechaCierre());
    }
}
