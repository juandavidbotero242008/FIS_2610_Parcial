package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Acta: resultados, ganador y cierre")
class ActaTest {

    @Test
    @DisplayName("constructor: exige mesa y fecha de cierre")
    void constructorInvalidoFalla() {
        Mesa mesa = new Mesa(1, 100);
        assertThrows(IllegalArgumentException.class, () -> new Acta(null, Fixtures.FECHA_CIERRE));
        assertThrows(IllegalArgumentException.class, () -> new Acta(mesa, null));
    }

    @Test
    @DisplayName("agregarResultado: registra los votos del candidato")
    void agregarResultadoValido() {
        Mesa mesa = new Mesa(1, 100);
        Candidato candidato = Fixtures.candidato("1");
        Acta acta = new Acta(mesa, Fixtures.FECHA_CIERRE);
        acta.agregarResultado(candidato, 25);
        assertEquals(25, acta.votosDe(candidato));
    }

    @Test
    @DisplayName("agregarResultado: rechaza candidato nulo")
    void agregarResultadoNuloFalla() {
        Acta acta = new Acta(new Mesa(1, 100), Fixtures.FECHA_CIERRE);
        assertThrows(IllegalArgumentException.class, () -> acta.agregarResultado(null, 10));
    }

    @Test
    @DisplayName("agregarResultado: rechaza votos negativos (caso limite)")
    void agregarResultadoNegativoFalla() {
        Acta acta = new Acta(new Mesa(1, 100), Fixtures.FECHA_CIERRE);
        assertThrows(IllegalArgumentException.class,
                () -> acta.agregarResultado(Fixtures.candidato("1"), -1));
    }

    @Test
    @DisplayName("agregarResultado: rechaza un candidato duplicado")
    void agregarResultadoDuplicadoFalla() {
        Mesa mesa = new Mesa(1, 100);
        Candidato candidato = Fixtures.candidato("1");
        Acta acta = new Acta(mesa, Fixtures.FECHA_CIERRE);
        acta.agregarResultado(candidato, 10);
        assertThrows(ValidacionElectoralException.class,
                () -> acta.agregarResultado(candidato, 5));
    }

    @Test
    @DisplayName("agregarResultado: no permite modificar un acta cerrada")
    void agregarResultadoTrasCerrarFalla() {
        Acta acta = Fixtures.actaCerrada(new Mesa(1, 100), Fixtures.candidato("1"), 10);
        assertThrows(ValidacionElectoralException.class,
                () -> acta.agregarResultado(Fixtures.candidato("2"), 5));
    }

    @Test
    @DisplayName("totalVotos: acta vacia suma cero (caso limite)")
    void totalVotosVacio() {
        assertEquals(0, Fixtures.actaVacia(new Mesa(1, 100)).totalVotos());
    }

    @Test
    @DisplayName("totalVotos: suma todos los resultados")
    void totalVotosSuma() {
        Acta acta = Fixtures.actaCerrada(new Mesa(1, 100),
                Fixtures.candidato("1"), 10, Fixtures.candidato("2"), 15);
        assertEquals(25, acta.totalVotos());
    }

    @Test
    @DisplayName("votosDe: retorna cero si el candidato no aparece (caso limite)")
    void votosDeCandidatoAusente() {
        Acta acta = Fixtures.actaVacia(new Mesa(1, 100));
        assertEquals(0, acta.votosDe(Fixtures.candidato("99")));
    }

    @Test
    @DisplayName("votosDe: retorna los votos del candidato registrado")
    void votosDeCandidatoPresente() {
        Candidato candidato = Fixtures.candidato("1");
        Acta acta = Fixtures.actaCerrada(new Mesa(1, 100), candidato, 42);
        assertEquals(42, acta.votosDe(candidato));
    }

    @Test
    @DisplayName("votosDe: rechaza candidato nulo")
    void votosDeNuloFalla() {
        Acta acta = Fixtures.actaVacia(new Mesa(1, 100));
        assertThrows(IllegalArgumentException.class, () -> acta.votosDe(null));
    }

    @Test
    @DisplayName("candidatoGanador: retorna el candidato con mas votos")
    void ganadorUnico() {
        Candidato primero = Fixtures.candidato("1");
        Candidato segundo = Fixtures.candidato("2");
        Acta acta = Fixtures.actaCerrada(new Mesa(1, 100), primero, 30, segundo, 20);
        assertEquals(Optional.of(primero), acta.candidatoGanador());
    }

    @Test
    @DisplayName("candidatoGanador: empate retorna vacio (caso limite)")
    void ganadorEmpatado() {
        Acta acta = Fixtures.actaCerrada(new Mesa(1, 100),
                Fixtures.candidato("1"), 30, Fixtures.candidato("2"), 30);
        assertTrue(acta.candidatoGanador().isEmpty());
    }

    @Test
    @DisplayName("candidatoGanador: acta vacia retorna vacio (caso limite)")
    void ganadorSinResultados() {
        assertTrue(Fixtures.actaVacia(new Mesa(1, 100)).candidatoGanador().isEmpty());
    }

    @Test
    @DisplayName("cerrar: deja el acta cerrada")
    void cerrarValido() {
        Acta acta = Fixtures.acta(new Mesa(1, 100), Fixtures.candidato("1"), 10);
        assertFalse(acta.estaCerrada());
        acta.cerrar();
        assertTrue(acta.estaCerrada());
    }

    @Test
    @DisplayName("cerrar: no se puede cerrar un acta sin resultados (caso limite)")
    void cerrarSinResultadosFalla() {
        Acta acta = Fixtures.actaVacia(new Mesa(1, 100));
        assertThrows(ValidacionElectoralException.class, acta::cerrar);
    }

    @Test
    @DisplayName("cerrar: no se puede cerrar dos veces")
    void cerrarDosVecesFalla() {
        Acta acta = Fixtures.actaCerrada(new Mesa(1, 100), Fixtures.candidato("1"), 10);
        assertThrows(ValidacionElectoralException.class, acta::cerrar);
    }

    @Test
    @DisplayName("getResultados: conserva los resultados registrados")
    void resultadosConservanRegistros() {
        Candidato primero = Fixtures.candidato("1");
        Candidato segundo = Fixtures.candidato("2");
        Acta acta = Fixtures.actaCerrada(new Mesa(1, 100), primero, 10, segundo, 15);
        assertEquals(2, acta.getResultados().size());
        assertEquals(10, acta.getResultados().get(primero));
        assertEquals(15, acta.getResultados().get(segundo));
    }

    @Test
    @DisplayName("getResultados: la vista es inmutable")
    void resultadosInmutables() {
        Acta acta = Fixtures.actaCerrada(new Mesa(1, 100), Fixtures.candidato("1"), 10);
        assertThrows(UnsupportedOperationException.class,
                () -> acta.getResultados().put(Fixtures.candidato("2"), 5));
    }

    @Test
    @DisplayName("getMesa y getFechaCierre: conservan los datos del acta")
    void gettersConservanDatos() {
        Mesa mesa = new Mesa(1, 100);
        LocalDateTime fecha = LocalDateTime.of(2026, 3, 8, 18, 30);
        Acta acta = new Acta(mesa, fecha);
        assertEquals(mesa, acta.getMesa());
        assertEquals(fecha, acta.getFechaCierre());
    }
}
