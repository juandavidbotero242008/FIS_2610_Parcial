package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.dominio.enums.EstadoReclamacion;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Reclamacion presentada por un partido o un testigo electoral sobre los
 * resultados consignados en un acta.
 *
 * <p>El tramite sigue el flujo RADICADA -> EN_REVISION -> RESUELTA, con la
 * posibilidad de rechazo desde los estados abiertos. Las transiciones
 * invalidas se rechazan para preservar la trazabilidad.</p>
 */
public class Reclamacion {

    private final String id;
    private final Acta acta;
    private final Partido partido;
    private final TestigoElectoral testigo;
    private final String motivo;
    private final LocalDateTime fechaRadicacion;
    private EstadoReclamacion estado;
    private String decision;

    public Reclamacion(String id, Acta acta, Partido partido, TestigoElectoral testigo, String motivo) {
        this(id, acta, partido, testigo, motivo, LocalDateTime.now());
    }

    public Reclamacion(String id, Acta acta, Partido partido, TestigoElectoral testigo,
                       String motivo, LocalDateTime fechaRadicacion) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la reclamacion es obligatorio");
        }
        if (acta == null) {
            throw new IllegalArgumentException("El acta reclamada es obligatoria");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de la reclamacion es obligatorio");
        }
        if (partido == null && testigo == null) {
            throw new ValidacionElectoralException(
                    "La reclamacion debe ser presentada por un partido o por un testigo electoral");
        }
        if (fechaRadicacion == null) {
            throw new IllegalArgumentException("La fecha de radicacion es obligatoria");
        }
        this.id = id;
        this.acta = acta;
        this.partido = partido;
        this.testigo = testigo;
        this.motivo = motivo;
        this.fechaRadicacion = fechaRadicacion;
        this.estado = EstadoReclamacion.RADICADA;
    }

    public String getId() {
        return id;
    }

    public Acta getActa() {
        return acta;
    }

    public Partido getPartido() {
        return partido;
    }

    public TestigoElectoral getTestigo() {
        return testigo;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFechaRadicacion() {
        return fechaRadicacion;
    }

    public EstadoReclamacion getEstado() {
        return estado;
    }

    public String getDecision() {
        return decision;
    }

    /** Pasa la reclamacion de RADICADA a EN_REVISION. */
    public void iniciarRevision() {
        if (estado != EstadoReclamacion.RADICADA) {
            throw new ValidacionElectoralException(
                    "Solo una reclamacion RADICADA puede pasar a revision; estado actual: " + estado);
        }
        estado = EstadoReclamacion.EN_REVISION;
    }

    /** Resuelve la reclamacion cuando ya esta en revision. */
    public void resolver(String decision) {
        if (estado != EstadoReclamacion.EN_REVISION) {
            throw new ValidacionElectoralException(
                    "Solo una reclamacion EN_REVISION puede resolverse; estado actual: " + estado);
        }
        if (decision == null || decision.isBlank()) {
            throw new IllegalArgumentException("La decision de la reclamacion es obligatoria");
        }
        this.decision = decision;
        this.estado = EstadoReclamacion.RESUELTA;
    }

    /** Rechaza la reclamacion desde un estado abierto. */
    public void rechazar(String motivoRechazo) {
        if (estado != EstadoReclamacion.RADICADA && estado != EstadoReclamacion.EN_REVISION) {
            throw new ValidacionElectoralException(
                    "Solo una reclamacion abierta puede rechazarse; estado actual: " + estado);
        }
        if (motivoRechazo == null || motivoRechazo.isBlank()) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio");
        }
        this.decision = motivoRechazo;
        this.estado = EstadoReclamacion.RECHAZADA;
    }

    /** Indica si la reclamacion aun admite gestion. */
    public boolean estaAbierta() {
        return estado == EstadoReclamacion.RADICADA || estado == EstadoReclamacion.EN_REVISION;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Reclamacion reclamacion)) {
            return false;
        }
        return Objects.equals(id, reclamacion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reclamacion{" + "id='" + id + '\'' + ", estado=" + estado + '}';
    }
}
