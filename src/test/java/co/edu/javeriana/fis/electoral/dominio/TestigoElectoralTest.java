package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TestigoElectoral: acreditacion por partido")
class TestigoElectoralTest {

    @Test
    @DisplayName("constructor: rechaza datos obligatorios invalidos")
    void constructorInvalidoFalla() {
        Partido partido = Fixtures.partido("P1");
        assertThrows(IllegalArgumentException.class,
                () -> new TestigoElectoral("", "Ana", partido));
        assertThrows(IllegalArgumentException.class,
                () -> new TestigoElectoral("123", " ", partido));
        assertThrows(IllegalArgumentException.class,
                () -> new TestigoElectoral("123", "Ana", null));
    }

    @Test
    @DisplayName("cambiarPartido: actualiza el partido acreditante")
    void cambiarPartidoValido() {
        Partido original = Fixtures.partido("P1");
        Partido nuevo = Fixtures.partido("P2");
        TestigoElectoral testigo = Fixtures.testigo("1", original);
        testigo.cambiarPartido(nuevo);
        assertEquals(nuevo, testigo.getPartido());
    }

    @Test
    @DisplayName("cambiarPartido: rechaza partido nulo")
    void cambiarPartidoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> Fixtures.testigo("1", Fixtures.partido("P1")).cambiarPartido(null));
    }

    @Test
    @DisplayName("cambiarPartido: rechaza el mismo partido")
    void cambiarAlMismoPartidoFalla() {
        Partido partido = Fixtures.partido("P1");
        TestigoElectoral testigo = Fixtures.testigo("1", partido);
        assertThrows(ValidacionElectoralException.class, () -> testigo.cambiarPartido(partido));
    }

    @Test
    @DisplayName("esDelPartido: verdadero para el partido que lo acredita")
    void esDelPartidoVerdadero() {
        Partido partido = Fixtures.partido("P1");
        assertTrue(Fixtures.testigo("1", partido).esDelPartido(partido));
    }

    @Test
    @DisplayName("esDelPartido: falso para otro partido")
    void esDelPartidoFalso() {
        TestigoElectoral testigo = Fixtures.testigo("1", Fixtures.partido("P1"));
        assertFalse(testigo.esDelPartido(Fixtures.partido("P2")));
    }

    @Test
    @DisplayName("esDelPartido: rechaza partido nulo")
    void esDelPartidoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> Fixtures.testigo("1", Fixtures.partido("P1")).esDelPartido(null));
    }

    @Test
    @DisplayName("getDocumento y getNombre: conservan los datos basicos")
    void gettersConservanDatos() {
        TestigoElectoral testigo = new TestigoElectoral("123", "Luis", Fixtures.partido("P1"));
        assertEquals("123", testigo.getDocumento());
        assertEquals("Luis", testigo.getNombre());
    }
}
