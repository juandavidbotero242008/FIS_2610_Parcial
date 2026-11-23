package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.dominio.enums.TipoIncidente;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Incidente: registro y cierre de situaciones de la jornada")
class IncidenteTest {

    private Incidente incidente(TipoIncidente tipo) {
        return new Incidente("I-1", tipo, "Descripcion", new Mesa(1, 100));
    }

    @Test
    @DisplayName("resolver: marca el incidente como resuelto con observacion")
    void resolverValido() {
        Incidente incidente = incidente(TipoIncidente.LOGISTICO);
        incidente.resolver("Se reemplazo la urna");
        assertTrue(incidente.estaResuelto());
        assertEquals("Se reemplazo la urna", incidente.getObservacion());
    }

    @Test
    @DisplayName("resolver: no se puede resolver dos veces")
    void resolverDosVecesFalla() {
        Incidente incidente = incidente(TipoIncidente.LOGISTICO);
        incidente.resolver("Cerrado");
        assertThrows(ValidacionElectoralException.class, () -> incidente.resolver("Otra vez"));
    }

    @Test
    @DisplayName("resolver: exige observacion de cierre")
    void resolverSinObservacionFalla() {
        Incidente incidente = incidente(TipoIncidente.LOGISTICO);
        assertThrows(IllegalArgumentException.class, () -> incidente.resolver(" "));
    }

    @Test
    @DisplayName("reabrir: vuelve a dejar el incidente abierto")
    void reabrirValido() {
        Incidente incidente = incidente(TipoIncidente.LOGISTICO);
        incidente.resolver("Cerrado");
        incidente.reabrir();
        assertFalse(incidente.estaResuelto());
        assertNull(incidente.getObservacion());
    }

    @Test
    @DisplayName("reabrir: falla si el incidente nunca se resolvio")
    void reabrirSinResolverFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> incidente(TipoIncidente.LOGISTICO).reabrir());
    }

    @Test
    @DisplayName("esCritico: orden publico es critico")
    void ordenPublicoEsCritico() {
        assertTrue(incidente(TipoIncidente.ORDEN_PUBLICO).esCritico());
    }

    @Test
    @DisplayName("esCritico: una falla tecnica no es critica")
    void fallaTecnicaNoEsCritica() {
        assertFalse(incidente(TipoIncidente.FALLA_TECNICA).esCritico());
    }

    @Test
    @DisplayName("esCritico: una irregularidad es critica")
    void irregularidadEsCritica() {
        assertTrue(incidente(TipoIncidente.IRREGULARIDAD).esCritico());
    }

    @Test
    @DisplayName("estaResuelto: inicia en falso")
    void iniciaSinResolver() {
        assertFalse(incidente(TipoIncidente.LOGISTICO).estaResuelto());
    }

    @Test
    @DisplayName("constructor: rechaza datos obligatorios invalidos")
    void constructorInvalidoFalla() {
        Mesa mesa = new Mesa(1, 100);
        assertThrows(IllegalArgumentException.class,
                () -> new Incidente("", TipoIncidente.LOGISTICO, "Descripcion", mesa));
        assertThrows(IllegalArgumentException.class,
                () -> new Incidente("I-1", null, "Descripcion", mesa));
        assertThrows(IllegalArgumentException.class,
                () -> new Incidente("I-1", TipoIncidente.LOGISTICO, " ", mesa));
        assertThrows(IllegalArgumentException.class,
                () -> new Incidente("I-1", TipoIncidente.LOGISTICO, "Descripcion", null));
        assertThrows(IllegalArgumentException.class,
                () -> new Incidente("I-1", TipoIncidente.LOGISTICO, "Descripcion", mesa, null));
    }

    @Test
    @DisplayName("constructor: por defecto usa la fecha actual")
    void fechaPorDefecto() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        Incidente incidente = new Incidente("I-1", TipoIncidente.LOGISTICO, "Descripcion", new Mesa(1, 100));
        assertTrue(incidente.getFecha().isAfter(antes));
    }

    @Test
    @DisplayName("getters: conservan los datos del incidente")
    void gettersConservanDatos() {
        Mesa mesa = new Mesa(1, 100);
        Incidente incidente = new Incidente("I-9", TipoIncidente.FALLA_TECNICA, "Sin energia", mesa);
        assertEquals("I-9", incidente.getId());
        assertEquals(TipoIncidente.FALLA_TECNICA, incidente.getTipo());
        assertEquals("Sin energia", incidente.getDescripcion());
        assertEquals(mesa, incidente.getMesa());
    }
}
