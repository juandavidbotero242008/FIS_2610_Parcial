package co.edu.javeriana.fis.electoral.patrones.comportamental.observer;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.dominio.Partido;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Observer: publicador y observadores concretos")
class PublicadorEventosTest {

    private EventoElectoral evento(String partido) {
        return new EventoElectoral(TipoEvento.RECLAMACION_RADICADA, "R-1", "Reclamacion", partido);
    }

    @Test
    @DisplayName("suscribir: incrementa el numero de observadores")
    void suscribirValido() {
        PublicadorEventos publicador = new PublicadorEventos();
        publicador.suscribir(new ObservadorAuditoria());
        assertEquals(1, publicador.totalObservadores());
    }

    @Test
    @DisplayName("suscribir: rechaza un observador duplicado")
    void suscribirDuplicadoFalla() {
        PublicadorEventos publicador = new PublicadorEventos();
        ObservadorAuditoria auditoria = new ObservadorAuditoria();
        publicador.suscribir(auditoria);
        assertThrows(ValidacionElectoralException.class, () -> publicador.suscribir(auditoria));
    }

    @Test
    @DisplayName("suscribir: rechaza observador nulo")
    void suscribirNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> new PublicadorEventos().suscribir(null));
    }

    @Test
    @DisplayName("desuscribir: reduce el numero de observadores")
    void desuscribirValido() {
        PublicadorEventos publicador = new PublicadorEventos();
        ObservadorAuditoria auditoria = new ObservadorAuditoria();
        publicador.suscribir(auditoria);
        publicador.desuscribir(auditoria);
        assertEquals(0, publicador.totalObservadores());
    }

    @Test
    @DisplayName("desuscribir: falla si el observador no esta suscrito")
    void desuscribirAusenteFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> new PublicadorEventos().desuscribir(new ObservadorAuditoria()));
    }

    @Test
    @DisplayName("desuscribir: rechaza observador nulo")
    void desuscribirNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> new PublicadorEventos().desuscribir(null));
    }

    @Test
    @DisplayName("publicar: notifica a todos los observadores")
    void publicarNotificaATodos() {
        PublicadorEventos publicador = new PublicadorEventos();
        ObservadorAuditoria auditoria = new ObservadorAuditoria();
        ObservadorAnalitica analitica = new ObservadorAnalitica();
        publicador.suscribir(auditoria);
        publicador.suscribir(analitica);
        publicador.publicar(evento("Partido P1"));
        assertEquals(1, auditoria.totalEventos());
        assertEquals(1, analitica.conteo(TipoEvento.RECLAMACION_RADICADA));
    }

    @Test
    @DisplayName("publicar: sin observadores no falla (caso limite)")
    void publicarSinObservadores() {
        new PublicadorEventos().publicar(evento("Partido P1"));
    }

    @Test
    @DisplayName("publicar: rechaza evento nulo")
    void publicarNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> new PublicadorEventos().publicar(null));
    }

    @Test
    @DisplayName("publicar: un observador puede desuscribirse al recibir")
    void observadorPuedeDesuscribirseAlRecibir() {
        PublicadorEventos publicador = new PublicadorEventos();
        ObservadorElectoral autoDesuscrito = new ObservadorElectoral() {
            @Override
            public void actualizar(EventoElectoral evento) {
                publicador.desuscribir(this);
            }
        };
        ObservadorAuditoria auditoria = new ObservadorAuditoria();
        publicador.suscribir(autoDesuscrito);
        publicador.suscribir(auditoria);
        publicador.publicar(evento(null));
        assertEquals(1, publicador.totalObservadores());
        assertEquals(1, auditoria.totalEventos());
    }

    @Test
    @DisplayName("ObservadorAuditoria: almacena los eventos recibidos")
    void auditoriaAlmacenaEventos() {
        ObservadorAuditoria auditoria = new ObservadorAuditoria();
        auditoria.actualizar(evento(null));
        assertEquals(1, auditoria.getEventos().size());
        assertTrue(auditoria.getEventos().get(0).referencia().equals("R-1"));
    }

    @Test
    @DisplayName("ObservadorAuditoria: rechaza evento nulo")
    void auditoriaEventoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> new ObservadorAuditoria().actualizar(null));
    }

    @Test
    @DisplayName("ObservadorAnalitica: cuenta eventos por tipo")
    void analiticaCuentaPorTipo() {
        ObservadorAnalitica analitica = new ObservadorAnalitica();
        analitica.actualizar(evento(null));
        analitica.actualizar(evento(null));
        assertEquals(2, analitica.conteo(TipoEvento.RECLAMACION_RADICADA));
        assertEquals(2, analitica.totalEventos());
    }

    @Test
    @DisplayName("ObservadorAnalitica: tipo sin eventos retorna cero (caso limite)")
    void analiticaTipoSinEventos() {
        assertEquals(0, new ObservadorAnalitica().conteo(TipoEvento.ACTA_REGISTRADA));
    }

    @Test
    @DisplayName("ObservadorAnalitica: rechaza tipo nulo")
    void analiticaTipoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> new ObservadorAnalitica().conteo(null));
    }

    @Test
    @DisplayName("ObservadorPartido: filtra los eventos del partido")
    void partidoFiltraEventos() {
        Partido partido = Fixtures.partido("P1");
        ObservadorPartido observador = new ObservadorPartido(partido);
        observador.actualizar(evento(partido.getNombre()));
        observador.actualizar(evento("Otro partido"));
        assertEquals(1, observador.totalRecibidos());
    }

    @Test
    @DisplayName("ObservadorPartido: ignora eventos sin partido")
    void partidoIgnoraEventosSinPartido() {
        ObservadorPartido observador = new ObservadorPartido(Fixtures.partido("P1"));
        observador.actualizar(evento(null));
        assertEquals(0, observador.totalRecibidos());
    }

    @Test
    @DisplayName("ObservadorPartido: rechaza partido nulo")
    void partidoNuloFalla() {
        assertThrows(IllegalArgumentException.class, () -> new ObservadorPartido(null));
    }

    @Test
    @DisplayName("ObservadorPartido: rechaza evento nulo")
    void partidoEventoNuloFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> new ObservadorPartido(Fixtures.partido("P1")).actualizar(null));
    }

    @Test
    @DisplayName("EventoElectoral: valida los campos obligatorios")
    void eventoValidaCampos() {
        assertThrows(IllegalArgumentException.class,
                () -> new EventoElectoral(null, "R-1", "Detalle", null));
        assertThrows(IllegalArgumentException.class,
                () -> new EventoElectoral(TipoEvento.ACTA_REGISTRADA, " ", "Detalle", null));
        assertThrows(IllegalArgumentException.class,
                () -> new EventoElectoral(TipoEvento.ACTA_REGISTRADA, "R-1", " ", null));
    }
}
