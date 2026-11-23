package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.dominio.enums.EstadoReclamacion;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Reclamacion: tramite y transiciones validas")
class ReclamacionTest {

    private Acta acta() {
        Mesa mesa = Fixtures.mesaCerrada(1, 100);
        return Fixtures.actaCerrada(mesa, Fixtures.candidato("1"), 10);
    }

    private Reclamacion reclamacion(Partido partido) {
        return new Reclamacion("R-1", acta(), partido, null, "Conteo inconsistente");
    }

    @Test
    @DisplayName("constructor: exige partido o testigo")
    void constructorSinSolicitanteFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> new Reclamacion("R-1", acta(), null, null, "Motivo"));
    }

    @Test
    @DisplayName("constructor: acepta un testigo sin partido directo")
    void constructorConTestigoValido() {
        Partido partido = Fixtures.partido("P1");
        Reclamacion reclamacion = new Reclamacion("R-1", acta(), null,
                Fixtures.testigo("T1", partido), "Motivo");
        assertEquals(EstadoReclamacion.RADICADA, reclamacion.getEstado());
        assertEquals(partido, reclamacion.getTestigo().getPartido());
    }

    @Test
    @DisplayName("constructor: rechaza datos obligatorios invalidos")
    void constructorInvalidoFalla() {
        Partido partido = Fixtures.partido("P1");
        assertThrows(IllegalArgumentException.class,
                () -> new Reclamacion("", acta(), partido, null, "Motivo"));
        assertThrows(IllegalArgumentException.class,
                () -> new Reclamacion("R-1", null, partido, null, "Motivo"));
        assertThrows(IllegalArgumentException.class,
                () -> new Reclamacion("R-1", acta(), partido, null, " "));
        assertThrows(IllegalArgumentException.class,
                () -> new Reclamacion("R-1", acta(), partido, null, "Motivo", null));
    }

    @Test
    @DisplayName("iniciarRevision: pasa de RADICADA a EN_REVISION")
    void iniciarRevisionValida() {
        Reclamacion reclamacion = reclamacion(Fixtures.partido("P1"));
        reclamacion.iniciarRevision();
        assertEquals(EstadoReclamacion.EN_REVISION, reclamacion.getEstado());
    }

    @Test
    @DisplayName("iniciarRevision: falla si ya esta en revision")
    void iniciarRevisionDosVecesFalla() {
        Reclamacion reclamacion = reclamacion(Fixtures.partido("P1"));
        reclamacion.iniciarRevision();
        assertThrows(ValidacionElectoralException.class, reclamacion::iniciarRevision);
    }

    @Test
    @DisplayName("resolver: resuelve una reclamacion en revision")
    void resolverValido() {
        Reclamacion reclamacion = reclamacion(Fixtures.partido("P1"));
        reclamacion.iniciarRevision();
        reclamacion.resolver("Conteo corregido");
        assertEquals(EstadoReclamacion.RESUELTA, reclamacion.getEstado());
        assertEquals("Conteo corregido", reclamacion.getDecision());
    }

    @Test
    @DisplayName("resolver: falla si no ha iniciado revision")
    void resolverSinRevisionFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> reclamacion(Fixtures.partido("P1")).resolver("Decision"));
    }

    @Test
    @DisplayName("resolver: exige la decision")
    void resolverSinDecisionFalla() {
        Reclamacion reclamacion = reclamacion(Fixtures.partido("P1"));
        reclamacion.iniciarRevision();
        assertThrows(IllegalArgumentException.class, () -> reclamacion.resolver(" "));
    }

    @Test
    @DisplayName("rechazar: rechaza desde RADICADA")
    void rechazarDesdeRadicada() {
        Reclamacion reclamacion = reclamacion(Fixtures.partido("P1"));
        reclamacion.rechazar("Sin pruebas");
        assertEquals(EstadoReclamacion.RECHAZADA, reclamacion.getEstado());
    }

    @Test
    @DisplayName("rechazar: rechaza desde EN_REVISION")
    void rechazarDesdeRevision() {
        Reclamacion reclamacion = reclamacion(Fixtures.partido("P1"));
        reclamacion.iniciarRevision();
        reclamacion.rechazar("Sin pruebas");
        assertEquals(EstadoReclamacion.RECHAZADA, reclamacion.getEstado());
    }

    @Test
    @DisplayName("rechazar: falla si la reclamacion ya fue resuelta")
    void rechazarResueltaFalla() {
        Reclamacion reclamacion = reclamacion(Fixtures.partido("P1"));
        reclamacion.iniciarRevision();
        reclamacion.resolver("Conteo corregido");
        assertThrows(ValidacionElectoralException.class,
                () -> reclamacion.rechazar("Sin pruebas"));
    }

    @Test
    @DisplayName("rechazar: exige el motivo de rechazo")
    void rechazarSinMotivoFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> reclamacion(Fixtures.partido("P1")).rechazar(" "));
    }

    @Test
    @DisplayName("estaAbierta: verdadero en RADICADA y EN_REVISION")
    void estaAbiertaEnEstadosIniciales() {
        Reclamacion reclamacion = reclamacion(Fixtures.partido("P1"));
        assertTrue(reclamacion.estaAbierta());
        reclamacion.iniciarRevision();
        assertTrue(reclamacion.estaAbierta());
    }

    @Test
    @DisplayName("estaAbierta: falso despues de resolver")
    void cerradaTrasResolver() {
        Reclamacion reclamacion = reclamacion(Fixtures.partido("P1"));
        reclamacion.iniciarRevision();
        reclamacion.resolver("Conteo corregido");
        assertFalse(reclamacion.estaAbierta());
    }

    @Test
    @DisplayName("getActa y getMotivo: conservan los datos de radicacion")
    void gettersConservanDatos() {
        Acta acta = acta();
        Reclamacion reclamacion = new Reclamacion("R-7", acta, Fixtures.partido("P1"), null, "Motivo");
        assertEquals(acta, reclamacion.getActa());
        assertEquals("Motivo", reclamacion.getMotivo());
    }
}
