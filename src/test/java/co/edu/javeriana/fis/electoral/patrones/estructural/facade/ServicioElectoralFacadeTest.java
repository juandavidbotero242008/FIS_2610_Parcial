package co.edu.javeriana.fis.electoral.patrones.estructural.facade;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.dominio.Acta;
import co.edu.javeriana.fis.electoral.dominio.Candidato;
import co.edu.javeriana.fis.electoral.dominio.Departamento;
import co.edu.javeriana.fis.electoral.dominio.Mesa;
import co.edu.javeriana.fis.electoral.dominio.Partido;
import co.edu.javeriana.fis.electoral.dominio.ProcesoElectoral;
import co.edu.javeriana.fis.electoral.dominio.Reclamacion;
import co.edu.javeriana.fis.electoral.dominio.TestigoElectoral;
import co.edu.javeriana.fis.electoral.dominio.enums.EstadoReclamacion;
import co.edu.javeriana.fis.electoral.dominio.enums.TipoEleccion;
import co.edu.javeriana.fis.electoral.excepcion.TransicionEstadoInvalidaException;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import co.edu.javeriana.fis.electoral.patrones.comportamental.observer.ObservadorAnalitica;
import co.edu.javeriana.fis.electoral.patrones.comportamental.observer.ObservadorAuditoria;
import co.edu.javeriana.fis.electoral.patrones.comportamental.observer.ObservadorPartido;
import co.edu.javeriana.fis.electoral.patrones.comportamental.observer.PublicadorEventos;
import co.edu.javeriana.fis.electoral.patrones.comportamental.observer.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ServicioElectoralFacade: fachada de los casos de uso electorales")
class ServicioElectoralFacadeTest {

    private Mesa mesa;
    private ProcesoElectoral proceso;
    private PublicadorEventos publicador;
    private ObservadorAuditoria auditoria;
    private ServicioElectoralFacade facade;

    @BeforeEach
    void setUp() {
        mesa = Fixtures.mesaCerrada(1, 100);
        proceso = Fixtures.procesoConMesa(TipoEleccion.LEGISLATIVA, mesa);
        proceso.iniciar();
        publicador = new PublicadorEventos();
        auditoria = new ObservadorAuditoria();
        publicador.suscribir(auditoria);
        facade = new ServicioElectoralFacade(proceso, publicador);
    }

    private Acta actaValida() {
        return Fixtures.acta(mesa, Fixtures.candidato("1"), 40);
    }

    private Acta registrarActaValida() {
        Acta acta = actaValida();
        facade.registrarActa(acta);
        return acta;
    }

    private Reclamacion reclamacion(Acta acta, Partido partido) {
        return new Reclamacion("R-1", acta, partido, null, "Conteo inconsistente");
    }

    @Test
    @DisplayName("registrarActa: cierra el acta, la asocia a la mesa y publica el evento")
    void registrarActaExitosa() {
        Acta acta = registrarActaValida();
        assertTrue(acta.estaCerrada());
        assertSame(acta, mesa.getActa());
        assertTrue(facade.actaDe(mesa).isPresent());
        assertEquals(TipoEvento.ACTA_REGISTRADA, auditoria.getEventos().get(0).tipo());
    }

    @Test
    @DisplayName("registrarActa: falla si el proceso no ha iniciado")
    void registrarActaProcesoNoIniciadoFalla() {
        Mesa otraMesa = Fixtures.mesaCerrada(2, 100);
        ProcesoElectoral programado = Fixtures.procesoConMesa(TipoEleccion.LEGISLATIVA, otraMesa);
        ServicioElectoralFacade otraFachada = new ServicioElectoralFacade(programado, publicador);
        Acta acta = Fixtures.acta(otraMesa, Fixtures.candidato("1"), 10);
        assertThrows(TransicionEstadoInvalidaException.class, () -> otraFachada.registrarActa(acta));
    }

    @Test
    @DisplayName("registrarActa: falla si la mesa no pertenece al proceso")
    void registrarActaMesaAjenaFalla() {
        Mesa ajena = Fixtures.mesaCerrada(99, 100);
        Acta acta = Fixtures.acta(ajena, Fixtures.candidato("1"), 10);
        assertThrows(ValidacionElectoralException.class, () -> facade.registrarActa(acta));
    }

    @Test
    @DisplayName("registrarActa: falla si los votos superan el censo y no cierra el acta")
    void registrarActaExcedeCensoFalla() {
        Acta acta = Fixtures.acta(mesa, Fixtures.candidato("1"), 150);
        assertThrows(ValidacionElectoralException.class, () -> facade.registrarActa(acta));
        assertFalse(acta.estaCerrada());
        assertEquals(0, auditoria.totalEventos());
    }

    @Test
    @DisplayName("registrarActa: falla si la mesa aun no esta cerrada y no cierra el acta")
    void registrarActaMesaNoCerradaFalla() {
        Mesa abierta = new Mesa(2, 100);
        abierta.instalar();
        ProcesoElectoral procesoConMesaAbierta = Fixtures.procesoConMesa(TipoEleccion.LEGISLATIVA, abierta);
        procesoConMesaAbierta.iniciar();
        ServicioElectoralFacade otraFachada = new ServicioElectoralFacade(procesoConMesaAbierta, publicador);
        Acta acta = Fixtures.acta(abierta, Fixtures.candidato("1"), 10);
        assertThrows(TransicionEstadoInvalidaException.class, () -> otraFachada.registrarActa(acta));
        assertFalse(acta.estaCerrada());
    }

    @Test
    @DisplayName("registrarActa: no permite dos actas para la misma mesa")
    void registrarActaDuplicadaFalla() {
        registrarActaValida();
        Acta segunda = Fixtures.acta(mesa, Fixtures.candidato("2"), 20);
        assertThrows(ValidacionElectoralException.class, () -> facade.registrarActa(segunda));
    }

    @Test
    @DisplayName("registrarActa: rechaza acta nula")
    void registrarActaNulaFalla() {
        assertThrows(IllegalArgumentException.class, () -> facade.registrarActa(null));
    }

    @Test
    @DisplayName("radicarReclamacion: guarda la reclamacion y notifica al partido")
    void radicarReclamacionValida() {
        Acta acta = registrarActaValida();
        Partido partido = Fixtures.partido("P1");
        ObservadorPartido observadorPartido = new ObservadorPartido(partido);
        publicador.suscribir(observadorPartido);
        facade.radicarReclamacion(reclamacion(acta, partido));
        assertEquals(1, facade.getReclamaciones().size());
        assertEquals(1, observadorPartido.totalRecibidos());
        assertEquals(2, auditoria.totalEventos());
    }

    @Test
    @DisplayName("radicarReclamacion: usa el partido del testigo si no hay partido directo")
    void radicarReclamacionConTestigo() {
        Acta acta = registrarActaValida();
        Partido partido = Fixtures.partido("P2");
        TestigoElectoral testigo = Fixtures.testigo("T1", partido);
        ObservadorPartido observadorPartido = new ObservadorPartido(partido);
        publicador.suscribir(observadorPartido);
        facade.radicarReclamacion(new Reclamacion("R-2", acta, null, testigo, "Motivo"));
        assertEquals(1, observadorPartido.totalRecibidos());
    }

    @Test
    @DisplayName("radicarReclamacion: falla si el acta no esta registrada")
    void radicarReclamacionSinActaFalla() {
        Acta acta = actaValida();
        assertThrows(ValidacionElectoralException.class,
                () -> facade.radicarReclamacion(reclamacion(acta, Fixtures.partido("P1"))));
    }

    @Test
    @DisplayName("radicarReclamacion: no permite ids duplicados")
    void radicarReclamacionDuplicadaFalla() {
        Acta acta = registrarActaValida();
        Partido partido = Fixtures.partido("P1");
        facade.radicarReclamacion(reclamacion(acta, partido));
        assertThrows(ValidacionElectoralException.class,
                () -> facade.radicarReclamacion(reclamacion(acta, partido)));
    }

    @Test
    @DisplayName("radicarReclamacion: falla si el proceso ya cerro")
    void radicarReclamacionProcesoCerradoFalla() {
        Acta acta = registrarActaValida();
        proceso.pasarAEscrutinio();
        proceso.cerrar();
        assertThrows(TransicionEstadoInvalidaException.class,
                () -> facade.radicarReclamacion(reclamacion(acta, Fixtures.partido("P1"))));
    }

    @Test
    @DisplayName("radicarReclamacion: rechaza reclamacion nula")
    void radicarReclamacionNulaFalla() {
        assertThrows(IllegalArgumentException.class, () -> facade.radicarReclamacion(null));
    }

    @Test
    @DisplayName("iniciarRevision: cambia el estado de la reclamacion")
    void iniciarRevisionValida() {
        Acta acta = registrarActaValida();
        facade.radicarReclamacion(reclamacion(acta, Fixtures.partido("P1")));
        facade.iniciarRevision("R-1");
        assertEquals(EstadoReclamacion.EN_REVISION, facade.getReclamaciones().get(0).getEstado());
    }

    @Test
    @DisplayName("iniciarRevision: falla si la reclamacion no existe")
    void iniciarRevisionInexistenteFalla() {
        assertThrows(ValidacionElectoralException.class, () -> facade.iniciarRevision("NO-EXISTE"));
    }

    @Test
    @DisplayName("iniciarRevision: rechaza id vacio")
    void iniciarRevisionIdVacioFalla() {
        assertThrows(IllegalArgumentException.class, () -> facade.iniciarRevision(" "));
    }

    @Test
    @DisplayName("resolverReclamacion: resuelve y publica el evento de cierre")
    void resolverReclamacionValida() {
        Acta acta = registrarActaValida();
        Partido partido = Fixtures.partido("P1");
        facade.radicarReclamacion(reclamacion(acta, partido));
        facade.iniciarRevision("R-1");
        facade.resolverReclamacion("R-1", "Conteo corregido");
        assertEquals(EstadoReclamacion.RESUELTA, facade.getReclamaciones().get(0).getEstado());
        assertEquals(TipoEvento.RECLAMACION_RESUELTA, auditoria.getEventos().get(2).tipo());
    }

    @Test
    @DisplayName("resolverReclamacion: falla si la reclamacion no existe")
    void resolverReclamacionInexistenteFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> facade.resolverReclamacion("NO-EXISTE", "Decision"));
    }

    @Test
    @DisplayName("rechazarReclamacion: rechaza y publica el evento de cierre")
    void rechazarReclamacionValida() {
        Acta acta = registrarActaValida();
        facade.radicarReclamacion(reclamacion(acta, Fixtures.partido("P1")));
        facade.rechazarReclamacion("R-1", "Sin pruebas");
        assertEquals(EstadoReclamacion.RECHAZADA, facade.getReclamaciones().get(0).getEstado());
        assertEquals(TipoEvento.RECLAMACION_RESUELTA, auditoria.getEventos().get(2).tipo());
    }

    @Test
    @DisplayName("consolidar: calcula censo, votos y participacion del departamento")
    void consolidarDepartamento() {
        registrarActaValida();
        Departamento departamento = proceso.getDepartamentos().get(0);
        ResumenConsolidado resumen = facade.consolidar(departamento);
        assertEquals(100, resumen.censo());
        assertEquals(40, resumen.votos());
        assertEquals(40.0, resumen.participacion(), 0.001);
    }

    @Test
    @DisplayName("consolidar: unidad vacia retorna participacion cero (caso limite)")
    void consolidarVacio() {
        ResumenConsolidado resumen = facade.consolidar(Fixtures.departamento("99", "Vacio"));
        assertEquals(0, resumen.censo());
        assertEquals(0, resumen.votos());
        assertEquals(0.0, resumen.participacion(), 0.001);
    }

    @Test
    @DisplayName("consolidar: rechaza unidad nula")
    void consolidarNuloFalla() {
        assertThrows(IllegalArgumentException.class, () -> facade.consolidar(null));
    }

    @Test
    @DisplayName("actaDe: retorna vacio si la mesa no tiene acta (caso limite)")
    void actaDeMesaSinActa() {
        assertTrue(facade.actaDe(mesa).isEmpty());
    }

    @Test
    @DisplayName("actaDe: rechaza mesa nula")
    void actaDeNulaFalla() {
        assertThrows(IllegalArgumentException.class, () -> facade.actaDe(null));
    }

    @Test
    @DisplayName("observador de analitica: cuenta los eventos de la fachada")
    void analiticaRecibeEventos() {
        ObservadorAnalitica analitica = new ObservadorAnalitica();
        publicador.suscribir(analitica);
        Acta acta = registrarActaValida();
        facade.radicarReclamacion(reclamacion(acta, Fixtures.partido("P1")));
        assertEquals(1, analitica.conteo(TipoEvento.ACTA_REGISTRADA));
        assertEquals(1, analitica.conteo(TipoEvento.RECLAMACION_RADICADA));
    }

    @Test
    @DisplayName("constructor: exige proceso y publicador")
    void constructorInvalidoFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> new ServicioElectoralFacade(null, publicador));
        assertThrows(IllegalArgumentException.class,
                () -> new ServicioElectoralFacade(proceso, null));
    }

    @Test
    @DisplayName("getActas: refleja las actas registradas")
    void getActasReflejaRegistros() {
        assertEquals(0, facade.getActas().size());
        registrarActaValida();
        assertEquals(1, facade.getActas().size());
    }
}
