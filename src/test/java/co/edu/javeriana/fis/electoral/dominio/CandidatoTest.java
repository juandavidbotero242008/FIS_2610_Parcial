package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Candidato: respaldos partidistas y estado de candidatura")
class CandidatoTest {

    @Test
    @DisplayName("constructor: rechaza documento o nombre vacios")
    void constructorInvalidoFalla() {
        assertThrows(IllegalArgumentException.class, () -> new Candidato("", "Ana"));
        assertThrows(IllegalArgumentException.class, () -> new Candidato("123", " "));
    }

    @Test
    @DisplayName("agregarPartido: vincula el respaldo del partido")
    void agregarPartidoValido() {
        Candidato candidato = Fixtures.candidato("1");
        Partido partido = Fixtures.partido("P1");
        candidato.agregarPartido(partido);
        assertTrue(candidato.estaRespaldadoPor(partido));
    }

    @Test
    @DisplayName("agregarPartido: rechaza partido nulo")
    void agregarPartidoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> Fixtures.candidato("1").agregarPartido(null));
    }

    @Test
    @DisplayName("agregarPartido: rechaza un respaldo duplicado")
    void agregarPartidoDuplicadoFalla() {
        Candidato candidato = Fixtures.candidato("1");
        Partido partido = Fixtures.partido("P1");
        candidato.agregarPartido(partido);
        assertThrows(ValidacionElectoralException.class, () -> candidato.agregarPartido(partido));
    }

    @Test
    @DisplayName("retirarPartido: retira uno de varios respaldos")
    void retirarPartidoValido() {
        Candidato candidato = Fixtures.candidato("1");
        Partido partido1 = Fixtures.partido("P1");
        Partido partido2 = Fixtures.partido("P2");
        candidato.agregarPartido(partido1);
        candidato.agregarPartido(partido2);
        candidato.retirarPartido(partido1);
        assertFalse(candidato.estaRespaldadoPor(partido1));
        assertTrue(candidato.estaRespaldadoPor(partido2));
    }

    @Test
    @DisplayName("retirarPartido: falla si el partido no respalda al candidato")
    void retirarPartidoAusenteFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> Fixtures.candidato("1").retirarPartido(Fixtures.partido("P1")));
    }

    @Test
    @DisplayName("retirarPartido: no deja sin respaldo a una candidatura activa")
    void retirarUltimoPartidoActivoFalla() {
        Candidato candidato = Fixtures.candidato("1");
        Partido partido = Fixtures.partido("P1");
        candidato.agregarPartido(partido);
        assertThrows(ValidacionElectoralException.class, () -> candidato.retirarPartido(partido));
        assertEquals(1, candidato.getPartidos().size());
    }

    @Test
    @DisplayName("retirarPartido: un candidato retirado puede quedar sin partidos (caso limite)")
    void retirarUltimoPartidoInactivoPermitido() {
        Candidato candidato = Fixtures.candidato("1");
        Partido partido = Fixtures.partido("P1");
        candidato.agregarPartido(partido);
        candidato.retirarCandidatura();
        candidato.retirarPartido(partido);
        assertTrue(candidato.getPartidos().isEmpty());
    }

    @Test
    @DisplayName("retirarPartido: rechaza partido nulo")
    void retirarPartidoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> Fixtures.candidato("1").retirarPartido(null));
    }

    @Test
    @DisplayName("estaRespaldadoPor: retorna falso si no hay respaldo (caso limite)")
    void sinRespaldoRetornaFalso() {
        assertFalse(Fixtures.candidato("1").estaRespaldadoPor(Fixtures.partido("P1")));
    }

    @Test
    @DisplayName("estaRespaldadoPor: rechaza partido nulo")
    void estaRespaldadoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> Fixtures.candidato("1").estaRespaldadoPor(null));
    }

    @Test
    @DisplayName("retirarCandidatura: cambia el estado a inactivo")
    void retirarCandidaturaValida() {
        Candidato candidato = Fixtures.candidato("1");
        candidato.retirarCandidatura();
        assertFalse(candidato.isActivo());
    }

    @Test
    @DisplayName("retirarCandidatura: no se puede retirar dos veces")
    void retirarCandidaturaDosVecesFalla() {
        Candidato candidato = Fixtures.candidato("1");
        candidato.retirarCandidatura();
        assertThrows(ValidacionElectoralException.class, candidato::retirarCandidatura);
    }

    @Test
    @DisplayName("reactivarCandidatura: reactiva un candidato retirado")
    void reactivarValida() {
        Candidato candidato = Fixtures.candidato("1");
        candidato.retirarCandidatura();
        candidato.reactivarCandidatura();
        assertTrue(candidato.isActivo());
    }

    @Test
    @DisplayName("reactivarCandidatura: falla si el candidato ya esta activo")
    void reactivarActivoFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> Fixtures.candidato("1").reactivarCandidatura());
    }

    @Test
    @DisplayName("getDocumento y getNombre: conservan los datos basicos")
    void gettersConservanDatos() {
        Candidato candidato = new Candidato("123", "Ana Perez");
        assertEquals("123", candidato.getDocumento());
        assertEquals("Ana Perez", candidato.getNombre());
    }
}
