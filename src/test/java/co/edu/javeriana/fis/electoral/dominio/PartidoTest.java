package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Partido: vinculo bidireccional con candidatos")
class PartidoTest {

    @Test
    @DisplayName("constructor: rechaza id o nombre vacios")
    void constructorInvalidoFalla() {
        assertThrows(IllegalArgumentException.class, () -> new Partido("", "Partido"));
        assertThrows(IllegalArgumentException.class, () -> new Partido("P1", " "));
    }

    @Test
    @DisplayName("constructor: conserva id y nombre")
    void constructorConservaDatos() {
        Partido partido = new Partido("P1", "Partido Verde");
        assertEquals("P1", partido.getId());
        assertEquals("Partido Verde", partido.getNombre());
    }

    @Test
    @DisplayName("agregarCandidato: sincroniza el respaldo en el candidato")
    void agregarCandidatoValido() {
        Partido partido = Fixtures.partido("P1");
        Candidato candidato = Fixtures.candidato("1");
        partido.agregarCandidato(candidato);
        assertTrue(partido.tieneCandidato(candidato));
        assertTrue(candidato.estaRespaldadoPor(partido));
    }

    @Test
    @DisplayName("agregarCandidato: rechaza candidato nulo")
    void agregarCandidatoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> Fixtures.partido("P1").agregarCandidato(null));
    }

    @Test
    @DisplayName("agregarCandidato: rechaza un candidato duplicado")
    void agregarCandidatoDuplicadoFalla() {
        Partido partido = Fixtures.partido("P1");
        Candidato candidato = Fixtures.candidato("1");
        partido.agregarCandidato(candidato);
        assertThrows(ValidacionElectoralException.class,
                () -> partido.agregarCandidato(candidato));
    }

    @Test
    @DisplayName("retirarCandidato: desvincula cuando tiene otro respaldo")
    void retirarCandidatoValido() {
        Partido partido1 = Fixtures.partido("P1");
        Partido partido2 = Fixtures.partido("P2");
        Candidato candidato = Fixtures.candidato("1");
        partido1.agregarCandidato(candidato);
        partido2.agregarCandidato(candidato);
        partido1.retirarCandidato(candidato);
        assertFalse(partido1.tieneCandidato(candidato));
        assertFalse(candidato.estaRespaldadoPor(partido1));
        assertTrue(candidato.estaRespaldadoPor(partido2));
    }

    @Test
    @DisplayName("retirarCandidato: falla si el candidato no pertenece al partido")
    void retirarCandidatoAjenoFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> Fixtures.partido("P1").retirarCandidato(Fixtures.candidato("1")));
    }

    @Test
    @DisplayName("retirarCandidato: no deja sin respaldo a un candidato activo")
    void retirarUltimoRespaldoFalla() {
        Partido partido = Fixtures.partido("P1");
        Candidato candidato = Fixtures.candidato("1");
        partido.agregarCandidato(candidato);
        assertThrows(ValidacionElectoralException.class,
                () -> partido.retirarCandidato(candidato));
        assertEquals(1, partido.numeroCandidatos());
    }

    @Test
    @DisplayName("retirarCandidato: rechaza candidato nulo")
    void retirarCandidatoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> Fixtures.partido("P1").retirarCandidato(null));
    }

    @Test
    @DisplayName("numeroCandidatos: un partido sin candidatos retorna cero (caso limite)")
    void numeroCandidatosVacio() {
        assertEquals(0, Fixtures.partido("P1").numeroCandidatos());
    }

    @Test
    @DisplayName("numeroCandidatos: cuenta los candidatos vinculados")
    void numeroCandidatosConDatos() {
        Partido partido = Fixtures.partido("P1");
        partido.agregarCandidato(Fixtures.candidato("1"));
        partido.agregarCandidato(Fixtures.candidato("2"));
        assertEquals(2, partido.numeroCandidatos());
    }

    @Test
    @DisplayName("tieneCandidato: retorna falso para un candidato no vinculado")
    void tieneCandidatoFalso() {
        assertFalse(Fixtures.partido("P1").tieneCandidato(Fixtures.candidato("1")));
    }

    @Test
    @DisplayName("tieneCandidato: rechaza candidato nulo")
    void tieneCandidatoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> Fixtures.partido("P1").tieneCandidato(null));
    }

    @Test
    @DisplayName("getCandidatos: conserva los candidatos agregados")
    void candidatosConDatos() {
        Partido partido = Fixtures.partido("P1");
        Candidato candidato = Fixtures.candidato("1");
        partido.agregarCandidato(candidato);
        assertEquals(List.of(candidato), partido.getCandidatos());
    }

    @Test
    @DisplayName("getCandidatos: la vista es inmutable")
    void candidatosInmutables() {
        Partido partido = Fixtures.partido("P1");
        assertThrows(UnsupportedOperationException.class,
                () -> partido.getCandidatos().add(Fixtures.candidato("1")));
    }
}
