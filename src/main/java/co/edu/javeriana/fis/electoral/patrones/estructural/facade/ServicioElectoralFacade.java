package co.edu.javeriana.fis.electoral.patrones.estructural.facade;

import co.edu.javeriana.fis.electoral.dominio.Acta;
import co.edu.javeriana.fis.electoral.dominio.Mesa;
import co.edu.javeriana.fis.electoral.dominio.ProcesoElectoral;
import co.edu.javeriana.fis.electoral.dominio.Reclamacion;
import co.edu.javeriana.fis.electoral.dominio.UnidadTerritorial;
import co.edu.javeriana.fis.electoral.dominio.enums.EstadoMesa;
import co.edu.javeriana.fis.electoral.excepcion.TransicionEstadoInvalidaException;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import co.edu.javeriana.fis.electoral.patrones.comportamental.observer.EventoElectoral;
import co.edu.javeriana.fis.electoral.patrones.comportamental.observer.PublicadorEventos;
import co.edu.javeriana.fis.electoral.patrones.comportamental.observer.TipoEvento;
import co.edu.javeriana.fis.electoral.patrones.comportamental.state.EstadoCancelado;
import co.edu.javeriana.fis.electoral.patrones.comportamental.state.EstadoCerrado;
import co.edu.javeriana.fis.electoral.patrones.comportamental.state.EstadoEnCurso;
import co.edu.javeriana.fis.electoral.patrones.comportamental.state.EstadoEscrutinio;
import co.edu.javeriana.fis.electoral.patrones.creacional.factory.FabricaValidadores;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Fachada del subsistema electoral (patron <b>Facade</b>).
 *
 * <p>Los analistas y funcionarios necesitan registrar actas, radicar y
 * resolver reclamaciones y consolidar resultados. Sin la fachada tendrian que
 * coordinar el validador del tipo de eleccion, el ciclo de vida de las mesas,
 * los repositorios en memoria y el publicador de eventos. Esta clase ofrece
 * una interfaz unica y de alto nivel para esos casos de uso.</p>
 */
public class ServicioElectoralFacade {

    private final ProcesoElectoral proceso;
    private final PublicadorEventos publicador;
    private final Map<Mesa, Acta> actas = new HashMap<>();
    private final Map<String, Reclamacion> reclamaciones = new HashMap<>();

    public ServicioElectoralFacade(ProcesoElectoral proceso, PublicadorEventos publicador) {
        if (proceso == null) {
            throw new IllegalArgumentException("El proceso electoral es obligatorio");
        }
        if (publicador == null) {
            throw new IllegalArgumentException("El publicador de eventos es obligatorio");
        }
        this.proceso = proceso;
        this.publicador = publicador;
    }

    /**
     * Registra el acta de cierre de una mesa.
     *
     * <p>Orquesta: valida la etapa del proceso, verifica que la mesa pertenezca
     * al proceso, aplica el validador del tipo de eleccion, cierra el acta, la
     * asocia a la mesa y publica el evento de auditoria.</p>
     */
    public void registrarActa(Acta acta) {
        if (acta == null) {
            throw new IllegalArgumentException("El acta es obligatoria");
        }
        if (!(proceso.getEstado() instanceof EstadoEnCurso)
                && !(proceso.getEstado() instanceof EstadoEscrutinio)) {
            throw new TransicionEstadoInvalidaException(
                    "Solo se registran actas durante la jornada o el escrutinio; estado actual: "
                            + proceso.estadoActual());
        }
        Mesa mesa = acta.getMesa();
        if (!proceso.mesas().contains(mesa)) {
            throw new ValidacionElectoralException("La mesa no pertenece al proceso electoral");
        }
        if (mesa.getEstado() != EstadoMesa.CERRADA) {
            throw new TransicionEstadoInvalidaException(
                    "La mesa debe estar cerrada antes de registrar su acta; estado actual: "
                            + mesa.getEstado());
        }
        if (actas.containsKey(mesa)) {
            throw new ValidacionElectoralException(
                    "La mesa " + mesa.getNumero() + " ya tiene un acta registrada");
        }
        FabricaValidadores.para(proceso.getTipoEleccion()).validar(acta);
        acta.cerrar();
        mesa.registrarActa(acta);
        actas.put(mesa, acta);
        publicador.publicar(new EventoElectoral(
                TipoEvento.ACTA_REGISTRADA,
                "MESA-" + mesa.getNumero(),
                "Acta registrada para la mesa " + mesa.getNumero(),
                null));
    }

    /** Radica una reclamacion sobre un acta ya registrada. */
    public void radicarReclamacion(Reclamacion reclamacion) {
        if (reclamacion == null) {
            throw new IllegalArgumentException("La reclamacion es obligatoria");
        }
        if (proceso.getEstado() instanceof EstadoCerrado
                || proceso.getEstado() instanceof EstadoCancelado) {
            throw new TransicionEstadoInvalidaException(
                    "No se aceptan reclamaciones en estado " + proceso.estadoActual());
        }
        if (reclamaciones.containsKey(reclamacion.getId())) {
            throw new ValidacionElectoralException(
                    "Ya existe una reclamacion con id " + reclamacion.getId());
        }
        if (!actas.containsValue(reclamacion.getActa())) {
            throw new ValidacionElectoralException(
                    "El acta reclamada no esta registrada en el proceso");
        }
        reclamaciones.put(reclamacion.getId(), reclamacion);
        String partido = reclamacion.getPartido() != null
                ? reclamacion.getPartido().getNombre()
                : reclamacion.getTestigo().getPartido().getNombre();
        publicador.publicar(new EventoElectoral(
                TipoEvento.RECLAMACION_RADICADA,
                reclamacion.getId(),
                "Reclamacion radicada: " + reclamacion.getMotivo(),
                partido));
    }

    /** Pasa una reclamacion a revision. */
    public void iniciarRevision(String idReclamacion) {
        buscarReclamacion(idReclamacion).iniciarRevision();
    }

    /** Resuelve una reclamacion en revision y publica el resultado. */
    public void resolverReclamacion(String idReclamacion, String decision) {
        Reclamacion reclamacion = buscarReclamacion(idReclamacion);
        reclamacion.resolver(decision);
        publicarCierre(reclamacion);
    }

    /** Rechaza una reclamacion abierta y publica el resultado. */
    public void rechazarReclamacion(String idReclamacion, String motivo) {
        Reclamacion reclamacion = buscarReclamacion(idReclamacion);
        reclamacion.rechazar(motivo);
        publicarCierre(reclamacion);
    }

    /** Consolida censo, votos y participacion de cualquier unidad territorial. */
    public ResumenConsolidado consolidar(UnidadTerritorial unidad) {
        return ResumenConsolidado.de(unidad);
    }

    public Optional<Acta> actaDe(Mesa mesa) {
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa es obligatoria");
        }
        return Optional.ofNullable(actas.get(mesa));
    }

    public List<Acta> getActas() {
        return List.copyOf(actas.values());
    }

    public List<Reclamacion> getReclamaciones() {
        return List.copyOf(reclamaciones.values());
    }

    public ProcesoElectoral getProceso() {
        return proceso;
    }

    private Reclamacion buscarReclamacion(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la reclamacion es obligatorio");
        }
        Reclamacion reclamacion = reclamaciones.get(id);
        if (reclamacion == null) {
            throw new ValidacionElectoralException("No existe la reclamacion " + id);
        }
        return reclamacion;
    }

    private void publicarCierre(Reclamacion reclamacion) {
        String partido = reclamacion.getPartido() != null
                ? reclamacion.getPartido().getNombre()
                : reclamacion.getTestigo().getPartido().getNombre();
        publicador.publicar(new EventoElectoral(
                TipoEvento.RECLAMACION_RESUELTA,
                reclamacion.getId(),
                "Reclamacion " + reclamacion.getEstado() + ": " + reclamacion.getDecision(),
                partido));
    }
}
